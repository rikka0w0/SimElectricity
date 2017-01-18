package simElectricity.Common.EnergyNet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import simElectricity.API.EnergyTile.ISEConductor;
import simElectricity.API.EnergyTile.ISEConstantPowerLoad;
import simElectricity.API.EnergyTile.ISEDiodeInput;
import simElectricity.API.EnergyTile.ISEDiodeOutput;
import simElectricity.API.EnergyTile.ISEGridNode;
import simElectricity.API.EnergyTile.ISEJunction;
import simElectricity.API.EnergyTile.ISERegulatorController;
import simElectricity.API.EnergyTile.ISERegulatorInput;
import simElectricity.API.EnergyTile.ISERegulatorOutput;
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.EnergyTile.ISETransformerPrimary;
import simElectricity.API.EnergyTile.ISETransformerSecondary;
import simElectricity.API.EnergyTile.ISEVoltageSource;
import simElectricity.Common.ConfigManager;
import simElectricity.Common.SEUtils;
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
    
    private int convergenceAssistantTiggerLevel;
    
    
	double Is = 1e-6;
	double Vt = 26e-6;
    
    
    public Simulator(String matrixSolverName, int maxIteration, int convergenceAssistantTiggerLevel, double epsilon, double Gnode, double Gpn){
    	this.maxIteration = maxIteration;
    	this.convergenceAssistantTiggerLevel = convergenceAssistantTiggerLevel;
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
    
    private double calcR(ISESimulatable cur, ISESimulatable neighbor){
    	if (cur instanceof ISEConductor){
    		ISEConductor curConductor = (ISEConductor) cur;
    		if (neighbor instanceof ISEConductor){
    			return curConductor.getResistance() + ((ISEConductor) neighbor).getResistance();
    		}else if (neighbor instanceof ISEJunction){
    			return curConductor.getResistance() + ((ISEJunction) neighbor).getResistance(curConductor);
    		}else{
    			return curConductor.getResistance();
    		}
    	} else if (cur instanceof ISEJunction){
    		ISEJunction curJunction = (ISEJunction) cur;
    		if (neighbor instanceof ISEConductor){
    			return ((ISEConductor) neighbor).getResistance() + curJunction.getResistance(neighbor);
    		}else if (neighbor instanceof ISEJunction){
    			return curJunction.getResistance(neighbor) + ((ISEJunction) neighbor).getResistance(curJunction);
    		}else if (neighbor instanceof ISEGridNode){
    			return curJunction.getResistance(neighbor);
    		}else{
    			throw new RuntimeException("Unaccptable conntection");
    		}
    	} else if (cur instanceof ISEGridNode){
    		ISEGridNode curGridNode = (ISEGridNode)cur;
    		if (neighbor instanceof ISEJunction){
    			return ((ISEJunction) neighbor).getResistance(curGridNode);
    		}else if (neighbor instanceof ISEGridNode){
    			return curGridNode.getResistance((ISEGridNode)neighbor);
    		}else {
    			throw new RuntimeException("Unaccptable conntection");
    		}
    	} else {
    		if (neighbor instanceof ISEConductor){
    			return ((ISEConductor)neighbor).getResistance();
    		}
    	}
    	
    	throw new RuntimeException("Unaccptable conntection");
    }

    double[] calcCurrents(double[] voltages, List<ISESimulatable> unknownVoltageNodes, BakaGraph<ISESimulatable> tileEntityGraph){
    	int matrixSize = unknownVoltageNodes.size();
    	
    	double[] currents = new double[matrixSize];   
    	
    	//Calculate the current flow into each node using their voltage
        for (int nodeIndex = 0; nodeIndex < matrixSize; nodeIndex++) {
        	ISESimulatable curNode = unknownVoltageNodes.get(nodeIndex);
         	
        	currents[nodeIndex] = -voltages[nodeIndex]*Gnode;
            
        	//Node - Node
			if (curNode instanceof ISEConductor || curNode instanceof ISEJunction || curNode instanceof ISEGridNode){
	        	for (ISESimulatable neighbor : tileEntityGraph.neighborListOf(curNode)){
	        		int iNeighbor = unknownVoltageNodes.indexOf(neighbor);
	        		
	        		currents[nodeIndex] -= (voltages[nodeIndex] - voltages[iNeighbor])/calcR(curNode, neighbor);
	        	}			
			}
			else{
				//For other components, its neighbor must be a ISEConductor
				List<ISESimulatable> neighborList = tileEntityGraph.neighborListOf(curNode);
				ISEConductor conductor = (ISEConductor) (neighborList.isEmpty() ? null : neighborList.get(0));
				
				if (conductor != null){
					int iConductor = unknownVoltageNodes.indexOf(conductor);
					currents[nodeIndex] -= (voltages[nodeIndex] - voltages[iConductor])/conductor.getResistance();		
				}
			}
			
			
			//Node - shunt and two port networks
			if (curNode instanceof ISEVoltageSource){
    			ISEVoltageSource vs = (ISEVoltageSource) curNode;
    			currents[nodeIndex] -= (voltages[nodeIndex] - vs.getOutputVoltage()) / vs.getResistance();
    		}else if (curNode instanceof ISEConstantPowerLoad){
    			ISEConstantPowerLoad load = (ISEConstantPowerLoad)curNode;
    			
    			double V = voltages[unknownVoltageNodes.indexOf(curNode)];
    			double Rcal = V*V/load.getRatedPower();
    			
    			if (Rcal > load.getMaximumResistance())
    				Rcal = load.getMaximumResistance();
    			if (Rcal < load.getMinimumResistance())
    				Rcal = load.getMinimumResistance();
    			
    			if (load.isEnabled())
    				currents[nodeIndex] -= V/Rcal;
    		}
    		
    		//Transformer
    		else if (curNode instanceof ISETransformerPrimary){
    			ISETransformerPrimary pri = (ISETransformerPrimary) curNode;
    			ISETransformerSecondary sec = pri.getSecondary();
    			double ratio = pri.getRatio();
    			double res = pri.getResistance();
    			int iSec = unknownVoltageNodes.indexOf(sec);
    			currents[nodeIndex] -= (voltages[nodeIndex]*ratio*ratio/res) - (voltages[iSec]*ratio/res);
    		}else if (curNode instanceof ISETransformerSecondary){
    			ISETransformerSecondary sec = (ISETransformerSecondary) curNode;
    			ISETransformerPrimary pri = sec.getPrimary();
    			double ratio = pri.getRatio();
    			double res = pri.getResistance();
    			int iPri = unknownVoltageNodes.indexOf(pri);
    			currents[nodeIndex] -= -(voltages[iPri]*ratio/res) + (voltages[nodeIndex]/res);
    		}
    		
			//Regulator
    		else if (curNode instanceof ISERegulatorInput){
    			ISERegulatorInput input = (ISERegulatorInput) curNode;
    			ISERegulatorOutput output = input.getOutput();
    			ISERegulatorController controller = input.getController();
    			
    			double Vi = voltages[nodeIndex];
    			double Vo = voltages[unknownVoltageNodes.indexOf(output)];	
    			double Vc = voltages[unknownVoltageNodes.indexOf(controller)];
    			double Ro = input.getOutputResistance();
    			double Dmax = controller.getDMax();
    			
    			double Ii = Vi*(Vc+Dmax)*(Vc+Dmax)/Ro - Vo*(Vc+Dmax)/Ro;
				
				currents[nodeIndex] -= Ii;
    		}else if (curNode instanceof ISERegulatorOutput){
    			ISERegulatorOutput output = (ISERegulatorOutput) curNode;
    			ISERegulatorInput input = output.getInput();
    			ISERegulatorController controller = input.getController();
    			
    			double Vi = voltages[unknownVoltageNodes.indexOf(input)];
    			double Vo = voltages[nodeIndex];	
    			double Vc = voltages[unknownVoltageNodes.indexOf(controller)];
    			double Ro = input.getOutputResistance();
    			double Dmax = controller.getDMax();
    			
    			double Io = -Vi*(Vc+Dmax)/Ro + Vo/Ro;
				
				currents[nodeIndex] -= Io;
    		}else if (curNode instanceof ISERegulatorController){
    			ISERegulatorController controller = (ISERegulatorController) curNode;
    			ISERegulatorInput input = controller.getInput();
    			ISERegulatorOutput output = input.getOutput();
    			
    			double Vo = voltages[unknownVoltageNodes.indexOf(output)];	
    			double Vc = voltages[nodeIndex];
    			double A = controller.getGain();
    			double Rs = controller.getRs();
    			double Rc = controller.getRc();
    			double Dmax = controller.getDMax();
    			
    			double Io = Vo*A/Rs + Vc/Rs + Dmax/Rs - input.getRegulatedVoltage()*A/Rs;
				
    			if (Vc > Vt*Math.log(Vt/Is/Rc))
    				Io += Vc/Rc;
    			else
    				Io += Is*Math.exp(Vc/Vt);
    			
				currents[nodeIndex] -= Io;
    		}
    		
    		
			//Diode
    		else if (curNode instanceof ISEDiodeInput){
    			ISEDiodeInput input = (ISEDiodeInput) curNode;
    			ISEDiodeOutput output = input.getOutput();
    			int iPri = nodeIndex;
    			int iSec = unknownVoltageNodes.indexOf(output);	
    			
    			double Vd = voltages[iPri]-voltages[iSec];
    			double Id;
    			

    			double Rmin = input.getForwardResistance();
    			if (Vd>Vt*Math.log(Vt/Is/Rmin)){
    				Id = Vd/Rmin + Vd*Gpn;
    			}else{
    				Id = Is*Math.exp(Vd/Vt) + Vd*Gpn;
    			}
    			
    			currents[nodeIndex] -= Id;
    		}else if (curNode instanceof ISEDiodeOutput){
    			ISEDiodeOutput output = (ISEDiodeOutput) curNode;
    			ISEDiodeInput input = output.getInput();
    			int iPri = unknownVoltageNodes.indexOf(input);
    			int iSec = nodeIndex;
    			
    			double Vd = voltages[iPri]-voltages[iSec];
    			double Id;
    			
    			double Rmin = input.getForwardResistance();
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

    void formJacobian(double[] voltages, List<ISESimulatable> unknownVoltageNodes, BakaGraph<ISESimulatable> tileEntityGraph){
    	int matrixSize = unknownVoltageNodes.size();
    	
    	matrix.newMatrix(matrixSize);
        // TODO Form Jacobian     
        for (int columnIndex = 0; columnIndex < matrixSize; columnIndex++) {
        	ISESimulatable columnNode = unknownVoltageNodes.get(columnIndex);
        	List<ISESimulatable> neighborList = tileEntityGraph.neighborListOf(columnNode);
        
        	double diagonalElement = Gnode;
        	
        	//Add conductance between nodes
        	for (ISESimulatable neighbor : neighborList){
        		int rowIndex = unknownVoltageNodes.indexOf(neighbor);
        		double G = 1.0D / calcR(columnNode, neighbor);
        		
        		diagonalElement += G;
        		
        		matrix.setElementValue(columnIndex, rowIndex, -G);
        	}
        	
        	//Process voltage sources and normal loads
        	if (columnNode instanceof ISEVoltageSource){
				ISEVoltageSource r = (ISEVoltageSource) columnNode;
				diagonalElement += 1.0D / r.getResistance();   						
			}
        	
        	//Constant power load
			if (columnNode instanceof ISEConstantPowerLoad){
				ISEConstantPowerLoad load = (ISEConstantPowerLoad)columnNode;
				double V = voltages[unknownVoltageNodes.indexOf(columnNode)];
				
    			double Rcal = V*V/load.getRatedPower();
    			
    			if (Rcal > load.getMaximumResistance())
    				Rcal = load.getMaximumResistance();
    			if (Rcal < load.getMinimumResistance())
    				Rcal = load.getMinimumResistance();
    			
    			if (load.isEnabled())
    				diagonalElement += 1.0D / Rcal;
			}
        	
			//Two port networks
        	//Transformer
        	else if (columnNode instanceof ISETransformerPrimary){
       			ISETransformerPrimary pri = (ISETransformerPrimary) columnNode;
       			int iPri = columnIndex;
       			int iSec = unknownVoltageNodes.indexOf(pri.getSecondary());
       			
       			double ratio = pri.getRatio();
       			double res = pri.getResistance();
       			//Primary diagonal element
       			diagonalElement += ratio*ratio/res;
       			
       			//Off-diagonal elements
       			matrix.setElementValue(iPri, iSec, -ratio / res);
       			matrix.setElementValue(iSec, iPri, -ratio / res);
			}
			else if (columnNode instanceof ISETransformerSecondary){
				//Secondary diagonal element
       			diagonalElement += 1.0D / ((ISETransformerSecondary) columnNode).getPrimary().getResistance();
			}
        	
        	//Diode
			else if (columnNode instanceof ISEDiodeInput){
    			ISEDiodeInput input = (ISEDiodeInput) columnNode;
    			
    			int iPri = columnIndex;
    			int iSec = unknownVoltageNodes.indexOf(input.getOutput());	
    			double Vd = voltages[iPri]-voltages[iSec];
    			
    			double Rmin = input.getForwardResistance();
    			
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
			else if (columnNode instanceof ISEDiodeOutput){
    			ISEDiodeInput input = ((ISEDiodeOutput) columnNode).getInput();

    			int iPri = unknownVoltageNodes.indexOf(input);
    			int iSec = columnIndex;
    			double Vd = voltages[iPri]-voltages[iSec];

    			double Rmin = input.getForwardResistance();

    			if (Vd>Vt*Math.log(Vt/Is/Rmin)){
    				diagonalElement += 1.0D/Rmin + Gpn;
    			}else{
    				diagonalElement += Is/Vt*Math.exp(Vd/Vt) + Gpn;
    			}
			}
        	
        	
        	//Regulator
			else if (columnNode instanceof ISERegulatorInput){
				ISERegulatorInput input = (ISERegulatorInput) columnNode;
    			ISERegulatorController controller = input.getController();
				
    			int iIn = columnIndex;
    			int iOut = unknownVoltageNodes.indexOf(input.getOutput());
    			int iCon = unknownVoltageNodes.indexOf(controller);
    			
    			double Vi = voltages[iIn];
    			double Vo = voltages[iOut];
    			double Vc = voltages[iCon];
    			double Ro = input.getOutputResistance();
    			double Dmax = controller.getDMax();
    			
    			diagonalElement += (Vc+Dmax)*(Vc+Dmax)/Ro;

    			matrix.setElementValue(iIn, iOut, -(Vc+Dmax)/Ro);
    			matrix.setElementValue(iOut, iIn, -(Vc+Dmax)/Ro);
    			matrix.setElementValue(iCon, iOut, -Vi/Ro);
    			matrix.setElementValue(iOut, iCon, controller.getGain()/controller.getRs());
    			matrix.setElementValue(iCon, iIn, (2*Vi*(Vc+Dmax) - Vo)/Ro);
			}
			else if (columnNode instanceof ISERegulatorOutput){
    			diagonalElement += 1.0D / ((ISERegulatorOutput) columnNode).getInput().getOutputResistance();
			}else if (columnNode instanceof ISERegulatorController){
				ISERegulatorController controller = (ISERegulatorController) columnNode;
				ISERegulatorInput input = controller.getInput();
    			
    			int iIn = unknownVoltageNodes.indexOf(input);
    			int iOut = unknownVoltageNodes.indexOf(input.getOutput());
    			int iCon = columnIndex;
    			
    			double Vi = voltages[iIn];
    			double Vo = voltages[iOut];
    			double Vc = voltages[iCon];
    			double Rs = controller.getRs();
    			double Rmin = controller.getRc();
    			
    			if (Vc>Vt*Math.log(Vt/Is/Rmin))
    				diagonalElement += 1.0D/Rs + 1.0D/Rmin;
    			else
    				diagonalElement += 1.0D/Rs + Is/Vt*Math.exp(Vc/Vt);
			}
        	
        	matrix.setElementValue(columnIndex, columnIndex, diagonalElement);
        }

        matrix.finishEditing();
    }
    
    public void run(BakaGraph<ISESimulatable> tileEntityGraph) {
        List<ISESimulatable> unknownVoltageNodes = new ArrayList<ISESimulatable>();
        unknownVoltageNodes.clear();
        unknownVoltageNodes.addAll(tileEntityGraph.vertexSet());
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
