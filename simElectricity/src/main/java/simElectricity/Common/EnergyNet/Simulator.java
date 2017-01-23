package simElectricity.Common.EnergyNet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.Common.ConfigManager;
import simElectricity.Common.SEUtils;
import simElectricity.Common.EnergyNet.Components.*;
import sun.security.ssl.Debug;

public class Simulator {
	
    //Stores result, the voltage of every nodes
    private Map<ISESimulatable, Double> voltageCache = new HashMap<ISESimulatable, Double>();
    //Matrix solving algorithm used to solve the problem
    private IMatrixResolver matrix;
    //Records the number of iterations during last iterating process
    private int iterations;
    //The allowed mismatch
    private double epsilon;
    //The maximum allowed number of iterations
    private int maxIteration;
    //The conductance placed between every node and the ground
    private double Gnode;
    
    private double Gpn;
    
    
    
	double Is = 1e-6;
	double Vt = 26e-6;
    
    
    public Simulator(String matrixSolverName, int maxIteration, double epsilon, double Gnode, double Gpn){
    	this.maxIteration = maxIteration;
    	this.epsilon = epsilon;
    	this.Gnode = Gnode;
    	this.Gpn = Gpn;
    	matrix = IMatrixResolver.MatrixHelper.newResolver(matrixSolverName);
    }
    
    public int getLastIteration(){
    	return iterations;
    }
    
    public int getTotalNonZeros(){
    	return matrix.getTotalNonZeros();
    }
    
    public double getVoltage(ISESimulatable Tile){
        if (voltageCache.containsKey(Tile)){
       	double voltage = voltageCache.get(Tile);
       	if (!Double.isNaN(voltage))	
       		return voltage;
        }
        return 0;
   }
    
    //Simulator------------------------------------------------------------------------
    private void attemptSolving(double[] b){
        if (!matrix.solve(b)){
        	throw new RuntimeException("Due to incorrect value of components, the energy net has been shutdown!");
        }
    }
    
    private double calcR(SEComponent cur, SEComponent neighbor){
    	if (cur instanceof Cable){
    		Cable curConductor = (Cable) cur;
    		if (neighbor instanceof Cable){
    			return curConductor.data.getResistance() + ((Cable) neighbor).data.getResistance();
    		}else if (neighbor instanceof Junction){
    			return curConductor.data.getResistance() + ((Junction) neighbor).data.getResistance(curConductor);
    		}else{
    			return curConductor.data.getResistance();
    		}
    	} else if (cur instanceof Junction){
    		Junction curJunction = (Junction) cur;
    		if (neighbor instanceof Cable){
    			return ((Cable) neighbor).data.getResistance() + curJunction.data.getResistance(neighbor);
    		}else if (neighbor instanceof Junction){
    			return curJunction.data.getResistance(neighbor) + ((Junction) neighbor).data.getResistance(curJunction);
    		}else if (neighbor instanceof GridNode){
    			return curJunction.data.getResistance(neighbor);
    		}else{
    			throw new RuntimeException("Unaccptable conntection");
    		}
    	} else if (cur instanceof GridNode){
    		GridNode curGridNode = (GridNode)cur;
    		if (neighbor instanceof Junction){
    			return ((Junction) neighbor).data.getResistance(curGridNode);
    		}else if (neighbor instanceof GridNode){
    			return curGridNode.resistances.get((GridNode)neighbor);
    		}else {
    			throw new RuntimeException("Unaccptable conntection");
    		}
    	} else {
    		if (neighbor instanceof Cable){
    			return ((Cable)neighbor).data.getResistance();
    		}
    	}
    	
    	throw new RuntimeException("Unaccptable conntection");
    }

    double[] calcCurrents(double[] voltages, List<SEComponent> unknownVoltageNodes, SEGraph tileEntityGraph){
    	int matrixSize = unknownVoltageNodes.size();
    	
    	double[] currents = new double[matrixSize];   
    	
    	//Calculate the current flow into each node using their voltage
        for (int nodeIndex = 0; nodeIndex < matrixSize; nodeIndex++) {
        	SEComponent curNode = unknownVoltageNodes.get(nodeIndex);
         	
        	currents[nodeIndex] = -voltages[nodeIndex]*Gnode;
            
        	//Node - Node
			if (curNode instanceof Cable || curNode instanceof Junction || curNode instanceof GridNode){
	        	for (SEComponent neighbor : curNode.neighbors){
	        		int iNeighbor = unknownVoltageNodes.indexOf(neighbor);
	        		
	        		currents[nodeIndex] -= (voltages[nodeIndex] - voltages[iNeighbor])/calcR(curNode, neighbor);
	        	}			
			}
			else{
				//For other components, its neighbor must be a Cable
				Cable conductor = (Cable) (curNode.neighbors.isEmpty() ? null : curNode.neighbors.get(0));
				
				if (conductor != null){
					int iConductor = unknownVoltageNodes.indexOf(conductor);
					currents[nodeIndex] -= (voltages[nodeIndex] - voltages[iConductor])/conductor.data.getResistance();		
				}
			}
			
			
			//Node - shunt and two port networks
			if (curNode instanceof VoltageSource){
    			VoltageSource vs = (VoltageSource) curNode;
    			currents[nodeIndex] -= (voltages[nodeIndex] - vs.data.getOutputVoltage()) / vs.data.getResistance();
    		}else if (curNode instanceof ConstantPowerLoad){
    			ConstantPowerLoad load = (ConstantPowerLoad)curNode;
    			
    			double V = voltages[unknownVoltageNodes.indexOf(curNode)];
    			double Rcal = V*V/load.data.getRatedPower();
    			
    			if (Rcal > load.data.getMaximumResistance())
    				Rcal = load.data.getMaximumResistance();
    			if (Rcal < load.data.getMinimumResistance())
    				Rcal = load.data.getMinimumResistance();
    			
    			if (load.data.isEnabled())
    				currents[nodeIndex] -= V/Rcal;
    		}
    		
    		//Transformer
    		else if (curNode instanceof TransformerPrimary){
    			TransformerPrimary pri = (TransformerPrimary) curNode;
    			TransformerSecondary sec = pri.secondary;
    			double ratio = pri.data.getRatio();
    			double res = pri.data.getInternalResistance();
    			int iSec = unknownVoltageNodes.indexOf(sec);
    			currents[nodeIndex] -= (voltages[nodeIndex]*ratio*ratio/res) - (voltages[iSec]*ratio/res);
    		}else if (curNode instanceof TransformerSecondary){
    			TransformerSecondary sec = (TransformerSecondary) curNode;
    			TransformerPrimary pri = sec.primary;
    			double ratio = pri.data.getRatio();
    			double res = pri.data.getInternalResistance();
    			int iPri = unknownVoltageNodes.indexOf(pri);
    			currents[nodeIndex] -= -(voltages[iPri]*ratio/res) + (voltages[nodeIndex]/res);
    		}
    		
			//Regulator
    		else if (curNode instanceof RegulatorInput){
    			RegulatorInput input = (RegulatorInput) curNode;
    			RegulatorOutput output = input.output;
    			RegulatorController controller = input.controller;
    			
    			double Vi = voltages[nodeIndex];
    			double Vo = voltages[unknownVoltageNodes.indexOf(output)];	
    			double Vc = voltages[unknownVoltageNodes.indexOf(controller)];
    			double Ro = input.data.getOutputResistance();
    			double Dmax = controller.input.data.getDMax();
    			
    			double Ii = Vi*(Vc+Dmax)*(Vc+Dmax)/Ro - Vo*(Vc+Dmax)/Ro;
				
				currents[nodeIndex] -= Ii;
    		}else if (curNode instanceof RegulatorOutput){
    			RegulatorOutput output = (RegulatorOutput) curNode;
    			RegulatorInput input = output.input;
    			RegulatorController controller = input.controller;
    			
    			double Vi = voltages[unknownVoltageNodes.indexOf(input)];
    			double Vo = voltages[nodeIndex];	
    			double Vc = voltages[unknownVoltageNodes.indexOf(controller)];
    			double Ro = input.data.getOutputResistance();
    			double Dmax = controller.input.data.getDMax();
    			
    			double Io = -Vi*(Vc+Dmax)/Ro + Vo/Ro;
				
				currents[nodeIndex] -= Io;
    		}else if (curNode instanceof RegulatorController){
    			RegulatorController controller = (RegulatorController) curNode;
    			RegulatorInput input = controller.input;
    			RegulatorOutput output = input.output;
    			
    			double Vo = voltages[unknownVoltageNodes.indexOf(output)];	
    			double Vc = voltages[nodeIndex];
    			double A = controller.input.data.getGain();
    			double Rs = controller.input.data.getRs();
    			double Rc = controller.input.data.getRc();
    			double Dmax = controller.input.data.getDMax();
    			
    			double Io = Vo*A/Rs + Vc/Rs + Dmax/Rs - input.data.getRegulatedVoltage()*A/Rs;
				
    			if (Vc > Vt*Math.log(Vt/Is/Rc))
    				Io += Vc/Rc;
    			else
    				Io += Is*Math.exp(Vc/Vt);
    			
				currents[nodeIndex] -= Io;
    		}
    		
    		
			//Diode
    		else if (curNode instanceof DiodeInput){
    			DiodeInput input = (DiodeInput) curNode;
    			DiodeOutput output = input.output;
    			int iPri = nodeIndex;
    			int iSec = unknownVoltageNodes.indexOf(output);	
    			
    			double Vd = voltages[iPri]-voltages[iSec];
    			double Id;
    			

    			double Rmin = input.data.getForwardResistance();
    			if (Vd>Vt*Math.log(Vt/Is/Rmin)){
    				Id = Vd/Rmin + Vd*Gpn;
    			}else{
    				Id = Is*Math.exp(Vd/Vt) + Vd*Gpn;
    			}
    			
    			currents[nodeIndex] -= Id;
    		}else if (curNode instanceof DiodeOutput){
    			DiodeOutput output = (DiodeOutput) curNode;
    			DiodeInput input = output.input;
    			int iPri = unknownVoltageNodes.indexOf(input);
    			int iSec = nodeIndex;
    			
    			double Vd = voltages[iPri]-voltages[iSec];
    			double Id;
    			
    			double Rmin = input.data.getForwardResistance();
    			if (Vd>Vt*Math.log(Vt/Is/Rmin)){
    				Id = Vd/Rmin + Vd*Gpn;
    			}else{
    				Id = Is*Math.exp(Vd/Vt) + Vd*Gpn;
    			}

    			
    			currents[nodeIndex] += Id;
    		}
        }
    	
    	return currents;
    }

    void formJacobian(double[] voltages, List<SEComponent> unknownVoltageNodes, SEGraph tileEntityGraph){
    	int matrixSize = unknownVoltageNodes.size();
    	
    	matrix.newMatrix(matrixSize);
        // TODO Form Jacobian     
        for (int columnIndex = 0; columnIndex < matrixSize; columnIndex++) {
        	SEComponent columnNode = unknownVoltageNodes.get(columnIndex);
        
        	double diagonalElement = Gnode;
        	
        	//Add conductance between nodes
        	for (SEComponent neighbor : columnNode.neighbors){
        		int rowIndex = unknownVoltageNodes.indexOf(neighbor);
        		double G = 1.0D / calcR(columnNode, neighbor);
        		
        		diagonalElement += G;
        		
        		matrix.setElementValue(columnIndex, rowIndex, -G);
        	}
        	
        	//Process voltage sources and normal loads
        	if (columnNode instanceof VoltageSource){
				VoltageSource r = (VoltageSource) columnNode;
				diagonalElement += 1.0D / r.data.getResistance();   						
			}
        	
        	//Constant power load
			if (columnNode instanceof ConstantPowerLoad){
				ConstantPowerLoad load = (ConstantPowerLoad)columnNode;
				double V = voltages[unknownVoltageNodes.indexOf(columnNode)];
				
    			double Rcal = V*V/load.data.getRatedPower();
    			
    			if (Rcal > load.data.getMaximumResistance())
    				Rcal = load.data.getMaximumResistance();
    			if (Rcal < load.data.getMinimumResistance())
    				Rcal = load.data.getMinimumResistance();
    			
    			if (load.data.isEnabled())
    				diagonalElement += 1.0D / Rcal;
			}
        	
			//Two port networks
        	//Transformer
        	else if (columnNode instanceof TransformerPrimary){
       			TransformerPrimary pri = (TransformerPrimary) columnNode;
       			int iPri = columnIndex;
       			int iSec = unknownVoltageNodes.indexOf(pri.secondary);
       			
       			double ratio = pri.data.getRatio();
       			double res = pri.data.getInternalResistance();
       			//Primary diagonal element
       			diagonalElement += ratio*ratio/res;
       			
       			//Off-diagonal elements
       			matrix.setElementValue(iPri, iSec, -ratio / res);
       			matrix.setElementValue(iSec, iPri, -ratio / res);
			}
			else if (columnNode instanceof TransformerSecondary){
				//Secondary diagonal element
       			diagonalElement += 1.0D / ((TransformerSecondary) columnNode).primary.data.getInternalResistance();
			}
        	
        	//Diode
			else if (columnNode instanceof DiodeInput){
    			DiodeInput input = (DiodeInput) columnNode;
    			
    			int iPri = columnIndex;
    			int iSec = unknownVoltageNodes.indexOf(input.output);	
    			double Vd = voltages[iPri]-voltages[iSec];
    			
    			double Rmin = input.data.getForwardResistance();
    			
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

    			int iPri = unknownVoltageNodes.indexOf(input);
    			int iSec = columnIndex;
    			double Vd = voltages[iPri]-voltages[iSec];

    			double Rmin = input.data.getForwardResistance();

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
    			int iOut = unknownVoltageNodes.indexOf(input.output);
    			int iCon = unknownVoltageNodes.indexOf(controller);
    			
    			double Vi = voltages[iIn];
    			double Vo = voltages[iOut];
    			double Vc = voltages[iCon];
    			double Ro = input.data.getOutputResistance();
    			double Dmax = controller.input.data.getDMax();
    			
    			diagonalElement += (Vc+Dmax)*(Vc+Dmax)/Ro;

    			matrix.setElementValue(iIn, iOut, -(Vc+Dmax)/Ro);
    			matrix.setElementValue(iOut, iIn, -(Vc+Dmax)/Ro);
    			matrix.setElementValue(iCon, iOut, -Vi/Ro);
    			matrix.setElementValue(iOut, iCon, controller.input.data.getGain()/controller.input.data.getRs());
    			matrix.setElementValue(iCon, iIn, (2*Vi*(Vc+Dmax) - Vo)/Ro);
			}
			else if (columnNode instanceof RegulatorOutput){
    			diagonalElement += 1.0D / ((RegulatorOutput) columnNode).input.data.getOutputResistance();
			}else if (columnNode instanceof RegulatorController){
				RegulatorController controller = (RegulatorController) columnNode;
				RegulatorInput input = controller.input;
    			
    			int iIn = unknownVoltageNodes.indexOf(input);
    			int iOut = unknownVoltageNodes.indexOf(input.output);
    			int iCon = columnIndex;
    			
    			double Vi = voltages[iIn];
    			double Vo = voltages[iOut];
    			double Vc = voltages[iCon];
    			double Rs = controller.input.data.getRs();
    			double Rmin = controller.input.data.getRc();
    			
    			if (Vc>Vt*Math.log(Vt/Is/Rmin))
    				diagonalElement += 1.0D/Rs + 1.0D/Rmin;
    			else
    				diagonalElement += 1.0D/Rs + Is/Vt*Math.exp(Vc/Vt);
			}
        	
        	matrix.setElementValue(columnIndex, columnIndex, diagonalElement);
        }

        matrix.finishEditing();
    }
    
    public void run(SEGraph tileEntityGraph) {
        List<SEComponent> unknownVoltageNodes = tileEntityGraph.getTerminalNodes();
    	int matrixSize = unknownVoltageNodes.size();

    	double[] voltages = new double[matrixSize];
    	double[] currents;   
    	   		
        iterations = 0;
        while(true) {
        	//Calculate the current flow into each node using their voltage
        	currents = calcCurrents(voltages, unknownVoltageNodes, tileEntityGraph);	//Current mismatch
        	
        	boolean keepGoing = false;
            
            for (int i = 0; i < matrixSize; i++) {
                if (Math.abs(currents[i]) > epsilon)
                	keepGoing = true;
            }      		

            
            if (keepGoing){
            	if (iterations > maxIteration){
            		SEUtils.logInfo("Maximum number of iteration has reached, something must go wrong!");
            		break;
            	}
            }else{
            	break;
            }
        	
        	formJacobian(voltages, unknownVoltageNodes, tileEntityGraph);
        	//matrix.print();
            attemptSolving(currents); 
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

        voltageCache.clear();
        for (int i = 0; i < matrixSize; i++) {
            voltageCache.put(unknownVoltageNodes.get(i), voltages[i]);
        }
        
        System.out.println("Run!" + String.valueOf(iterations));        
    }

    /*End of Simulator*/
}
