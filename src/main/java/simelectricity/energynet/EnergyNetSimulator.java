package simelectricity.energynet;

import java.util.Iterator;
import java.util.LinkedList;

import simelectricity.api.node.ISESimulatable;
import simelectricity.common.ConfigManager;
import simelectricity.common.SEUtils;
import simelectricity.energynet.matrix.IMatrixResolver;

import simelectricity.energynet.components.Cable;
import simelectricity.energynet.components.ConstantPowerLoad;
import simelectricity.energynet.components.DiodeInput;
import simelectricity.energynet.components.DiodeOutput;
import simelectricity.energynet.components.GridNode;
import simelectricity.energynet.components.SEComponent;
import simelectricity.energynet.components.TransformerPrimary;
import simelectricity.energynet.components.TransformerSecondary;
import simelectricity.energynet.components.VoltageSource;
import simelectricity.energynet.components.SwitchA;
import simelectricity.energynet.components.SwitchB;


public class EnergyNetSimulator{
    //Records the number of iterations during last iterating process
	protected int iterations;
	
	//The absolute tolerance
	protected final double epsilon;
    //The conductance placed between each PN junction(to alleviate convergence problem)
	protected final double Gpn;
    //Diode parameters for regulator controllers
	protected final double Vt = 26e-6;
	protected final double Is = 1e-6;
	
    //Matrix solving algorithm used to solve the problem
	protected final IMatrixResolver matrix;
	//Contains information about the grid
	protected final EnergyNetDataProvider dataProvider;
    
	protected EnergyNetSimulator(double epsilon, double Gpn,
			IMatrixResolver matrixSolver, EnergyNetDataProvider dataProvider){
		this.epsilon = epsilon;
		this.Gpn = Gpn;
		this.matrix = matrixSolver;
		this.dataProvider = dataProvider;
	}
	
	/**
	 * @param voltages input, node voltage array from last iteration
	 * @param currents output, return the new current mismatch
	 * @param iterator An iterator instance of the unknown voltage node linked list.
	 */
    private final void calcCurrents(double[] voltages, double[] currents, Iterator<SEComponent> iterator){   	    	
    	//Calculate the current flow into each node using their voltage
    	while(iterator.hasNext()){
    		SEComponent columnNode = iterator.next();
    		
    		//Node - Node
			Iterator<SEComponent> iteratorON = columnNode.optimizedNeighbors.iterator();
			Iterator<Double> iteratorR = columnNode.optimizedResistance.iterator();
			while (iteratorON.hasNext()){
				SEComponent neighbor = iteratorON.next();
        		double R = iteratorR.next();
        		currents[columnNode.index] -= (voltages[columnNode.index] - voltages[neighbor.index])/R;					
			}
			
			
			
			if (columnNode instanceof Cable){
				Cable cable = (Cable) columnNode;
				
				if (cable.hasShuntResistance)
					currents[columnNode.index] -= voltages[columnNode.index] / cable.shuntResistance;
				
				//Cable - GridNode interconnection
				if (cable.connectedGridNode != null && cable.isGridLinkEnabled)
					currents[columnNode.index] -= (voltages[columnNode.index] - voltages[cable.connectedGridNode.index])/cable.resistance;
			}
			
			else if (columnNode instanceof GridNode){
				GridNode gridNode = (GridNode) columnNode;
				
				//Cable - GridNode interconnection
				if (gridNode.interConnection != null && gridNode.interConnection.isGridLinkEnabled)
					currents[columnNode.index] -= (voltages[columnNode.index] - voltages[gridNode.interConnection.index])/gridNode.interConnection.resistance;	
				
				if (gridNode.type == GridNode.ISEGridNode_TransformerPrimary){
					GridNode pri = gridNode;
					GridNode sec = pri.complement;
					double ratio = pri.ratio;
					double res = pri.resistance;
					currents[columnNode.index] -= (voltages[pri.index]*ratio*ratio/res) - (voltages[sec.index]*ratio/res);
				}
				
				if (gridNode.type == GridNode.ISEGridNode_TransformerSecondary){
					GridNode sec = gridNode;
					GridNode pri = sec.complement;
					double ratio = pri.ratio;
					double res = pri.resistance;
					currents[columnNode.index] -= -(voltages[pri.index]*ratio/res) + (voltages[sec.index]/res);
				}
			}

			
			//Node - shunt and two port networks
			else if (columnNode instanceof VoltageSource){
    			VoltageSource vs = (VoltageSource) columnNode;
    			currents[columnNode.index] -= (voltages[columnNode.index] - vs.v) / vs.r;
    		}else if (columnNode instanceof ConstantPowerLoad){
    			ConstantPowerLoad load = (ConstantPowerLoad)columnNode;
    			
    			double V = voltages[columnNode.index];
    			double Rcal = V*V/ load.pRated;
    			
    			if (Rcal > load.rMax)
    				Rcal = load.rMax;
    			if (Rcal < load.rMin)
    				Rcal = load.rMin;
    			
    			if (load.enabled)
    				currents[columnNode.index] -= V/Rcal;
    		}
    		
			//Switch
    		else if (columnNode instanceof SwitchA){
    			SwitchA A = (SwitchA) columnNode;
    			SwitchB B = A.B;
    			
    			if (A.isOn)
    				currents[columnNode.index] -= (voltages[columnNode.index]-voltages[B.index])/A.resistance;
    		}else if (columnNode instanceof SwitchB){
    			SwitchB B = (SwitchB) columnNode;
    			SwitchA A = B.A;
    			
    			if (A.isOn)
    				currents[columnNode.index] -= (voltages[columnNode.index]-voltages[A.index])/A.resistance;
    		}
			
    		//Transformer
    		else if (columnNode instanceof TransformerPrimary){
    			TransformerPrimary pri = (TransformerPrimary) columnNode;
    			TransformerSecondary sec = pri.secondary;
    			double ratio = pri.ratio;
    			double res = pri.rsec;
    			currents[columnNode.index] -= (voltages[pri.index]*ratio*ratio/res) - (voltages[sec.index]*ratio/res);
    		}else if (columnNode instanceof TransformerSecondary){
    			TransformerSecondary sec = (TransformerSecondary) columnNode;
    			TransformerPrimary pri = sec.primary;
    			double ratio = pri.ratio;
    			double res = pri.rsec;
    			currents[columnNode.index] -= -(voltages[pri.index]*ratio/res) + (voltages[sec.index]/res);
    		}   		
    		
    		
			//Diode
    		else if (columnNode instanceof DiodeInput){
    			DiodeInput input = (DiodeInput) columnNode;
    			DiodeOutput output = input.output;
    			
    			double Vd = voltages[columnNode.index]-voltages[output.index];
    			
    			currents[columnNode.index] -= input.calcId(Vd) + Vd * Gpn;
    		}else if (columnNode instanceof DiodeOutput){
    			DiodeOutput output = (DiodeOutput) columnNode;
    			DiodeInput input = output.input;
    			
    			double Vd = voltages[input.index]-voltages[columnNode.index];

    			
    			currents[columnNode.index] += input.calcId(Vd) + Vd * Gpn;
    		}
    	}
    }

    private final void formJacobian(double[] voltages, Iterator<SEComponent> iterator){   	
    	matrix.newMatrix(voltages.length);

    	while (iterator.hasNext()){
    		SEComponent columnNode = iterator.next();
    		int columnIndex = columnNode.index;
    		
        	double diagonalElement = 0;
        	
        	//Add conductance between nodes
			Iterator<SEComponent> iteratorON = columnNode.optimizedNeighbors.iterator();
			Iterator<Double> iteratorR = columnNode.optimizedResistance.iterator();
			while (iteratorON.hasNext()){
				SEComponent neighbor = iteratorON.next();
				int rowIndex = neighbor.index;
        		double R = iteratorR.next();	
        		
        		diagonalElement += 1.0D / R;
        		
        		matrix.setElementValue(columnIndex, rowIndex, -1.0D / R);
			}
			
			
			
			
			//Cable - GridNode
			if (columnNode instanceof Cable){
				Cable cable = (Cable)columnNode;
				
				if (cable.hasShuntResistance)
					diagonalElement += 1.0D/cable.shuntResistance;
				
				if (cable.connectedGridNode != null && cable.isGridLinkEnabled){
					int iCable = columnIndex;
					int iGridNode = cable.connectedGridNode.index;
					
					//Diagonal element
					diagonalElement += 1.0D/cable.resistance;	
					
	       			//Off-diagonal elements
	       			matrix.setElementValue(iCable, iGridNode, -1.0D / cable.resistance);
	       			matrix.setElementValue(iGridNode, iCable, -1.0D / cable.resistance);
				}
			}else if (columnNode instanceof GridNode){
				GridNode gridNode = (GridNode) columnNode;
				
				if (gridNode.interConnection != null && gridNode.interConnection.isGridLinkEnabled){
					diagonalElement += 1.0D / gridNode.interConnection.resistance;
				}
				
				if (gridNode.type == GridNode.ISEGridNode_TransformerPrimary){
					GridNode pri = gridNode;
					GridNode sec = pri.complement;
					double ratio = pri.ratio;
					double res = pri.resistance;

	       			int iPri = pri.index;
	       			int iSec = sec.index;
	       	
	       			//Primary diagonal element
	       			diagonalElement += ratio*ratio/res;
	       			
	       			//Off-diagonal elements
	       			matrix.setElementValue(iPri, iSec, -ratio / res);
	       			matrix.setElementValue(iSec, iPri, -ratio / res);
				}
				
				if (gridNode.type == GridNode.ISEGridNode_TransformerSecondary){
					GridNode sec = gridNode;
					GridNode pri = sec.complement;
					double ratio = pri.ratio;
					double res = pri.resistance;
					
					diagonalElement += 1.0D / res;
				}
			}
			
        	
        	//Process voltage sources and resistive loads
			else if (columnNode instanceof VoltageSource){
				diagonalElement += 1.0D / ((VoltageSource) columnNode).r;   						
			}
        	
        	//Constant power load
			else if (columnNode instanceof ConstantPowerLoad){
				ConstantPowerLoad load = (ConstantPowerLoad)columnNode;
				double V = voltages[columnNode.index];
				
    			double Rcal = V*V/load.pRated;
    			
    			if (Rcal > load.rMax)
    				Rcal = load.rMax;
    			if (Rcal < load.rMin)
    				Rcal = load.rMin;
    			
    			if (load.enabled)
    				diagonalElement += 1.0D / Rcal;
			}
        	
			//Two port networks
			//Switch
			else if (columnNode instanceof SwitchA){
				SwitchA A = (SwitchA) columnNode;
				
				if (A.isOn){
					int iA = columnIndex;
					int iB = A.B.index;
					
					//Diagonal element
					diagonalElement += 1.0D/A.resistance;	
					
	       			//Off-diagonal elements
	       			matrix.setElementValue(iA, iB, -1.0D / A.resistance);
	       			matrix.setElementValue(iB, iA, -1.0D / A.resistance);
				}
			}else if (columnNode instanceof SwitchB){
				//Diagonal element
				if (((SwitchB) columnNode).A.isOn)
					diagonalElement += 1.0D / ((SwitchB) columnNode).A.resistance;
			}
			
			
        	//Transformer
        	else if (columnNode instanceof TransformerPrimary){
       			TransformerPrimary pri = (TransformerPrimary) columnNode;
       			int iPri = pri.index;
       			int iSec = pri.secondary.index;
       			
       			double ratio = pri.ratio;
       			double res = pri.rsec;
       			//Primary diagonal element
       			diagonalElement += ratio*ratio/res;
       			
       			//Off-diagonal elements
       			matrix.setElementValue(iPri, iSec, -ratio / res);
       			matrix.setElementValue(iSec, iPri, -ratio / res);
			}
			else if (columnNode instanceof TransformerSecondary){
				//Secondary diagonal element
       			diagonalElement += 1.0D / ((TransformerSecondary) columnNode).primary.rsec;
			}
        	
        	//Diode
			else if (columnNode instanceof DiodeInput){
    			DiodeInput input = (DiodeInput) columnNode;
    			
    			int iPri = columnIndex;
    			int iSec = input.output.index;	
    			double Vd = voltages[iPri]-voltages[iSec];
    			double Gd = input.calcG(Vd) + Gpn;
    			
    			diagonalElement += Gd;
    			matrix.setElementValue(iPri, iSec, -Gd);
    			matrix.setElementValue(iSec, iPri, -Gd);
			}
			else if (columnNode instanceof DiodeOutput){
    			DiodeInput input = ((DiodeOutput) columnNode).input;

    			int iPri = input.index;
    			int iSec = columnIndex;
    			double Vd = voltages[iPri]-voltages[iSec];
    			double Gd = input.calcG(Vd) + Gpn;
    			
    			diagonalElement += Gd ;
			}
        	
        	
        	matrix.setElementValue(columnIndex, columnIndex, diagonalElement);	
    	}

        matrix.finishEditing();
    }
    
    protected final void runSimulator(boolean optimizeGraph) {   	
    	if (optimizeGraph)
    		dataProvider.getTEGraph().optimizGraph();
    	
        LinkedList<SEComponent> unknownVoltageNodes = dataProvider.getTEGraph().getTerminalNodes();
        
    	int matrixSize = 0;
    	Iterator<SEComponent> iterator = unknownVoltageNodes.iterator();
    	while(iterator.hasNext()){
    		iterator.next().index = matrixSize;
    		matrixSize++;
    	}
    	
    	double[] voltages = new double[matrixSize];
    	double[] currents = new double[matrixSize];;
    	   		
        iterations = 0;
        while(true) {
        	for (int i = 0; i < matrixSize; i++)
        		currents[i] = 0;
        	//Calculate the current flow into each node using their voltage
        	calcCurrents(voltages, currents, unknownVoltageNodes.iterator());	//Current mismatch
        	
        	boolean keepGoing = false;
            
            for (int i = 0; i < matrixSize; i++) {
                if (Math.abs(currents[i]) > epsilon)
                	keepGoing = true;
            }      		

            
            if (keepGoing){
            	if (iterations > ConfigManager.maxIteration){
            		SEUtils.logError("Maximum number of iteration has reached, something must be wrong!", SEUtils.simulator);
            		break;
            	}
            }else{
            	break;
            }
        	
        	formJacobian(voltages, unknownVoltageNodes.iterator());
        	
        	String[] header = new String[unknownVoltageNodes.size()];
        	Iterator<SEComponent> it = unknownVoltageNodes.iterator();
        	while(it.hasNext()){
        		SEComponent comp = it.next();
        		header[comp.index] = comp.toString();
        	}
        	//matrix.print(header);
            if (!matrix.solve(currents)){
            	throw new RuntimeException("Due to incorrect value of components, the energy net has been shutdown!");
            }
            //currents is now deltaV
            
            //Debug.println("Iteration:", String.valueOf(iterations));
            for (int i = 0; i < matrixSize; i++) {
            	if (!Double.isNaN(currents[i]))
            		voltages[i] += currents[i];
            	//String[] temp = unknownVoltageNodes.get(i).toString().split("[.]");
            	//Debug.println(temp[temp.length-1].split("@")[0], String.valueOf(voltages[i]));
            }
      
            iterations++;
        }

        
        //Update voltage cache
    	dataProvider.getTEGraph().clearVoltageCache();
        for (SEComponent node: unknownVoltageNodes){
        	node.voltageCache = voltages[node.index];
        }
        
        SEUtils.logInfo("Calculation converges in " + String.valueOf(iterations) + " iterations.", SEUtils.simulator);        
    }
    
    public final synchronized static double getVoltage(ISESimulatable Tile){   
    	SEComponent node = (SEComponent) Tile;
    	if (node.eliminated){
    		if (node.optimizedNeighbors.size() == 2){
    			SEComponent A = node.optimizedNeighbors.getFirst();
    			SEComponent B = node.optimizedNeighbors.getLast();
    			double vA = A.voltageCache;
    			double vB = B.voltageCache;
    			double rA = node.optimizedResistance.getFirst();
    			double rB = node.optimizedResistance.getLast();
    			return vA - (vA-vB)*rA/(rA+rB);
    		}else if (node.optimizedNeighbors.size() == 1){
    			return node.optimizedNeighbors.getFirst().voltageCache;
    		}else if (node.optimizedNeighbors.size() == 0){
    			return 0;
    		}else{
    			throw new RuntimeException("WTF mate whats going on?!");
    		}
    	}else{
    		return node.voltageCache;
    	}
    }
    
	public final synchronized static double getCurrentMagnitude(ISESimulatable Tile) {
    	SEComponent node = (SEComponent) Tile;
    	if (node.eliminated){
    		if (node.optimizedNeighbors.size() == 2){
    			SEComponent A = node.optimizedNeighbors.getFirst();
    			SEComponent B = node.optimizedNeighbors.getLast();
    			double vA = A.voltageCache;
    			double vB = B.voltageCache;
    			double rA = node.optimizedResistance.getFirst();
    			double rB = node.optimizedResistance.getLast();
    			return Math.abs((vA-vB)/(rA+rB));
    		}else if (node.optimizedNeighbors.size() == 1){
    			return 0;
    		}else if (node.optimizedNeighbors.size() == 0){
    			return 0;
    		}else{
    			throw new RuntimeException("WTF mate whats going on?!");
    		}    		
    	}else if (node instanceof SwitchA){
    		SwitchA switchA = (SwitchA) node;
			double vA = switchA.voltageCache;
			double vB = switchA.B.voltageCache;
			return Math.abs((vA-vB)/switchA.resistance);
    	}else if (node instanceof SwitchB){
    		SwitchB switchB = (SwitchB) node;
			double vA = switchB.voltageCache;
			double vB = switchB.A.voltageCache;
			return Math.abs((vA-vB)/switchB.A.resistance);
    	}else if (node instanceof VoltageSource){
    		VoltageSource vs = (VoltageSource) node;
    		return Math.abs((vs.voltageCache-vs.v)/vs.r);
    	}
    	
		return Double.NaN;
	}
}
