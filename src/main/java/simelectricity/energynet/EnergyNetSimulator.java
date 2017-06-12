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
import simelectricity.energynet.components.RegulatorController;
import simelectricity.energynet.components.RegulatorInput;
import simelectricity.energynet.components.RegulatorOutput;
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
			
			
			//Cable - GridNode interconnection
			if (columnNode instanceof Cable){
				Cable cable = (Cable) columnNode;
				
				if (cable.connectedGridNode != null && cable.isGridLinkEnabled)
					currents[columnNode.index] -= (voltages[columnNode.index] - voltages[cable.connectedGridNode.index])/cable.resistance;
			}else if (columnNode instanceof GridNode){
				GridNode gridNode = (GridNode) columnNode;
				
				if (gridNode.interConnection != null && gridNode.interConnection.isGridLinkEnabled)
					currents[columnNode.index] -= (voltages[columnNode.index] - voltages[gridNode.interConnection.index])/gridNode.interConnection.resistance;	
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
    			currents[columnNode.index] -= (voltages[columnNode.index]*ratio*ratio/res) - (voltages[sec.index]*ratio/res);
    		}else if (columnNode instanceof TransformerSecondary){
    			TransformerSecondary sec = (TransformerSecondary) columnNode;
    			TransformerPrimary pri = sec.primary;
    			double ratio = pri.ratio;
    			double res = pri.rsec;
    			currents[columnNode.index] -= -(voltages[pri.index]*ratio/res) + (voltages[columnNode.index]/res);
    		}
    		
			//Regulator
    		else if (columnNode instanceof RegulatorInput){
    			RegulatorInput input = (RegulatorInput) columnNode;
    			RegulatorOutput output = input.output;
    			RegulatorController controller = input.controller;
    			
    			double Vi = voltages[columnNode.index];
    			double Vo = voltages[output.index];	
    			double Vc = voltages[controller.index];
    			double Ro = input.Ro;
    			double Dmax = input.Dmax;
    			
    			double Ii = Vi*(Vc+Dmax)*(Vc+Dmax)/Ro - Vo*(Vc+Dmax)/Ro;
				
				currents[columnNode.index] -= Ii;
    		}else if (columnNode instanceof RegulatorOutput){
    			RegulatorOutput output = (RegulatorOutput) columnNode;
    			RegulatorInput input = output.input;
    			RegulatorController controller = input.controller;
    			
    			double Vi = voltages[input.index];
    			double Vo = voltages[columnNode.index];	
    			double Vc = voltages[controller.index];
    			double Ro = input.Ro;
    			double Dmax = input.Dmax;
    			double Rdummy = input.Rdummy;
    			
    			double Io = -Vi*(Vc+Dmax)/Ro + Vo/Ro + Vo/Rdummy;
				
				currents[columnNode.index] -= Io;
    		}else if (columnNode instanceof RegulatorController){
    			RegulatorController controller = (RegulatorController) columnNode;
    			RegulatorInput input = controller.input;
    			RegulatorOutput output = input.output;
    			
    			double Vo = voltages[output.index];	
    			double Vc = voltages[columnNode.index];
    			double A = input.A;
    			double Rs = input.Rs;
    			double Rc = input.Rc;
    			double Dmax = input.Dmax;
    			
    			
    			double Io = Vo*A/Rs + Vc/Rs + Dmax/Rs - input.Vref*A/Rs;
				
    			if (Vc > Vt*Math.log(Vt/Is/Rc))
    				Io += Vc/Rc;
    			else
    				Io += Is*Math.exp(Vc/Vt);
    			
				currents[columnNode.index] -= Io;
    		}
    		
    		
			//Diode
    		else if (columnNode instanceof DiodeInput){
    			DiodeInput input = (DiodeInput) columnNode;
    			DiodeOutput output = input.output;
    			
    			double Vd = voltages[columnNode.index]-voltages[output.index];
    			double Vt = input.Vt;
    			double Is = input.Is;
    			double Id;
    			

    			double Rmin = input.Rs;
    			if (Vd>Vt*Math.log(Vt/Is/Rmin)){
    				Id = Vd/Rmin + Vd*Gpn;
    			}else{
    				Id = Is*Math.exp(Vd/Vt) + Vd*Gpn;
    			}
    			
    			currents[columnNode.index] -= Id;
    		}else if (columnNode instanceof DiodeOutput){
    			DiodeOutput output = (DiodeOutput) columnNode;
    			DiodeInput input = output.input;
    			
    			double Vd = voltages[input.index]-voltages[columnNode.index];
    			double Vt = input.Vt;
    			double Is = input.Is;
    			double Id;
    			
    			double Rmin = input.Rs;
    			if (Vd>Vt*Math.log(Vt/Is/Rmin)){
    				Id = Vd/Rmin + Vd*Gpn;
    			}else{
    				Id = Is*Math.exp(Vd/Vt) + Vd*Gpn;
    			}

    			
    			currents[columnNode.index] += Id;
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
			
			
			
			
			//Cable - GridNode interconnection
			if (columnNode instanceof Cable){
				Cable cable = (Cable)columnNode;
				
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
       			int iPri = columnIndex;
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
    			
    			double Vt = input.Vt;
    			double Is = input.Is;
    			double Rmin = input.Rs;
    			
    			double Gd;
    			
    			if (Vd>Vt*Math.log(Vt/Is/Rmin)){
    				Gd = 1.0D/Rmin + Gpn;
    				diagonalElement += 1.0D/Rmin + Gpn;
    			}else{
    				Gd = Is/Vt*Math.exp(Vd/Vt) + Gpn;
    				diagonalElement += Is/Vt*Math.exp(Vd/Vt) + Gpn;
    			}
    			
    			matrix.setElementValue(iPri, iSec, -Gd);
    			matrix.setElementValue(iSec, iPri, -Gd);
			}
			else if (columnNode instanceof DiodeOutput){
    			DiodeInput input = ((DiodeOutput) columnNode).input;

    			int iPri = input.index;
    			int iSec = columnIndex;
    			double Vd = voltages[iPri]-voltages[iSec];
    			
    			double Vt = input.Vt;
    			double Is = input.Is;
    			double Rmin = input.Rs;

    			if (Vd>Vt*Math.log(Vt/Is/Rmin)){
    				diagonalElement += 1.0D/Rmin + Gpn;
    			}else{
    				diagonalElement += Is/Vt*Math.exp(Vd/Vt) + Gpn;
    			}
			}
        	
        	
        	//Regulator
			else if (columnNode instanceof RegulatorInput){
				RegulatorInput input = (RegulatorInput) columnNode;
    			RegulatorController controller = input.controller;
				
    			int iIn = columnIndex;
    			int iOut = input.output.index;
    			int iCon = controller.index;
    			
    			double Vi = voltages[iIn];
    			double Vo = voltages[iOut];
    			double Vc = voltages[iCon];
    			double Ro = input.Ro;
    			double Dmax = controller.input.Dmax;
    			
    			diagonalElement += (Vc+Dmax)*(Vc+Dmax)/Ro;

    			matrix.setElementValue(iIn, iOut, -(Vc+Dmax)/Ro);
    			matrix.setElementValue(iOut, iIn, -(Vc+Dmax)/Ro);
    			matrix.setElementValue(iCon, iOut, -Vi/Ro);
    			matrix.setElementValue(iOut, iCon, controller.input.A/controller.input.Rs);
    			matrix.setElementValue(iCon, iIn, (2*Vi*(Vc+Dmax) - Vo)/Ro);
			}else if (columnNode instanceof RegulatorOutput){
    			diagonalElement += 1.0D / ((RegulatorOutput) columnNode).input.Ro;
    			diagonalElement += 1.0D / ((RegulatorOutput) columnNode).input.Rdummy;
			}else if (columnNode instanceof RegulatorController){
				RegulatorController controller = (RegulatorController) columnNode;
				RegulatorInput input = controller.input;
    			
    			int iIn = input.index;
    			int iOut = input.output.index;
    			int iCon = columnIndex;
    			
    			double Vi = voltages[iIn];
    			double Vo = voltages[iOut];
    			double Vc = voltages[iCon];
    			double Rs = controller.input.Rs;
    			double Rmin = controller.input.Rc;
    			
    			if (Vc>Vt*Math.log(Vt/Is/Rmin))
    				diagonalElement += 1.0D/Rs + 1.0D/Rmin;
    			else
    				diagonalElement += 1.0D/Rs + Is/Vt*Math.exp(Vc/Vt);
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
        	//matrix.print();
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
    
    public final static double getVoltage(ISESimulatable Tile){   
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
    
	public final static double getCurrentMagnitude(ISESimulatable Tile) {
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
