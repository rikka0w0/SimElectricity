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
    
    private int convergenceAssistantTiggerLevel;
    
    
    public Simulator(String matrixSolverName, int maxIteration, int convergenceAssistantTiggerLevel, double epsilon, double Gnode){
    	this.maxIteration = maxIteration;
    	this.convergenceAssistantTiggerLevel = convergenceAssistantTiggerLevel;
    	this.epsilon = epsilon;
    	this.Gnode = Gnode;
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
    		}else if (curNode instanceof ISETransformerPrimary){
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
    		}else if (curNode instanceof ISERegulatorInput){
    			ISERegulatorInput pri = (ISERegulatorInput) curNode;
    			ISERegulatorOutput sec = pri.getOutput();
    			double Vmin = pri.getMinimumInputVoltage();
    			double Vreg = pri.getRegulatedVoltage();
    			double Vmax = pri.getMaximumInputVoltage();
    			double deltaV = pri.getOutputRipple();
    			double res = pri.getOutputResistance();
    			int iPri = nodeIndex;
    			int iSec = unknownVoltageNodes.indexOf(sec);

    			double ratio = 0;
    			double I;
    			
				if (voltages[iPri] > Vmin){
					ratio = deltaV/(Vmax-Vmin);
					double c = Vreg - (Vmax+Vmin)*deltaV/(Vmax-Vmin)/2;
					I = (ratio*ratio*voltages[iPri] - ratio*voltages[iSec] + 2*ratio*c + c*c/voltages[iPri] - c*voltages[iSec]/voltages[iPri]) / res;
				}else{
					ratio = (Vreg-0.5*deltaV)/Vmin;
					I = (voltages[iPri]*ratio*ratio/res) - (voltages[iSec]*ratio/res);
    			}
    			
				
				currents[nodeIndex] -= I;
    		}else if (curNode instanceof ISERegulatorOutput){
    			ISERegulatorOutput sec = (ISERegulatorOutput) curNode;
    			ISERegulatorInput pri = sec.getInput();
    			double Vmin = pri.getMinimumInputVoltage();
    			double Vreg = pri.getRegulatedVoltage();
    			double Vmax = pri.getMaximumInputVoltage();
    			double deltaV = pri.getOutputRipple();
    			double res = pri.getOutputResistance();
    			int iPri = unknownVoltageNodes.indexOf(pri);
    			int iSec = nodeIndex;
    			
    			double ratio = 0;
    			double I;
    			
				if (voltages[iPri] > Vmin){
					ratio = deltaV/(Vmax-Vmin);
					double c = Vreg - (Vmax+Vmin)*deltaV/(Vmax-Vmin)/2;
					I = -ratio*voltages[iPri]/res + voltages[iSec]/res - c/res;
				}else{
					ratio = (Vreg-0.5*deltaV)/Vmin;
					I = -(voltages[iPri]*ratio/res) + (voltages[iSec]/res);
    			}
				
			
				currents[nodeIndex] -= I;
    		}else if (curNode instanceof ISEDiodeInput){
    			ISEDiodeInput input = (ISEDiodeInput) curNode;
    			ISEDiodeOutput output = input.getOutput();
    			int iPri = nodeIndex;
    			int iSec = unknownVoltageNodes.indexOf(output);	
    			
    			double Vj = input.getVoltageDrop();
    			double Vd = voltages[iPri]-voltages[iSec];
    			double Id;
    			
    			if (Vd>Vj){
    				Id = Vd/input.getForwardResistance() - Vj/input.getForwardResistance() + Vj/input.getReverseResistance();
    			}else{
    				Id = Vd/input.getReverseResistance();
    			}
    			
    			currents[nodeIndex] -= Id;
    		}else if (curNode instanceof ISEDiodeOutput){
    			ISEDiodeOutput output = (ISEDiodeOutput) curNode;
    			ISEDiodeInput input = output.getInput();
    			int iPri = unknownVoltageNodes.indexOf(input);
    			int iSec = nodeIndex;
    			
    			double Vj = input.getVoltageDrop();
    			double Vd = voltages[iPri]-voltages[iSec];
    			double Id;
    			
    			if (Vd>Vj){
    				Id = Vd/input.getForwardResistance() - Vj/input.getForwardResistance() + Vj/input.getReverseResistance();
    			}else{
    				Id = Vd/input.getReverseResistance();
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
        	
        	if (columnNode instanceof ISETransformerPrimary){
       			ISETransformerPrimary pri = (ISETransformerPrimary) columnNode;
       			ISETransformerSecondary sec = pri.getSecondary();
       			int iSec = unknownVoltageNodes.indexOf(sec);
       			
       			double coef = -pri.getRatio() / pri.getResistance();
       			matrix.setElementValue(columnIndex, iSec, coef);
       			
			}
			else if (columnNode instanceof ISETransformerSecondary){
				ISETransformerSecondary sec = (ISETransformerSecondary) columnNode;
				ISETransformerPrimary pri = sec.getPrimary();
				int iPri = unknownVoltageNodes.indexOf(pri);
				
				double coef = -pri.getRatio() / pri.getResistance();
				matrix.setElementValue(columnIndex, iPri, coef);
			}
			else if (columnNode instanceof ISERegulatorInput){
				ISERegulatorInput pri = (ISERegulatorInput) columnNode;
				ISERegulatorOutput sec = pri.getOutput();
    			double Vmin = pri.getMinimumInputVoltage();
    			double Vreg = pri.getRegulatedVoltage();
    			double Vmax = pri.getMaximumInputVoltage();
    			double deltaV = pri.getOutputRipple();
    			double res = pri.getOutputResistance();
    			int iPri = columnIndex;
    			int iSec = unknownVoltageNodes.indexOf(sec);
    			
    			double coef;
    			if (voltages[iPri] > Vmin){
    				double ratio = deltaV/(Vmax-Vmin);
    				double c = Vreg - (Vmax+Vmin)*deltaV/(Vmax-Vmin)/2;
    				coef = -ratio / res;
    			}else{
    				double ratio = (Vreg-0.5*deltaV)/Vmin;
    				coef = -ratio / res;
    			}
    			
    			matrix.setElementValue(columnIndex, iSec, coef);	//columnIndex = iPri
			}
			else if (columnNode instanceof ISERegulatorOutput){
				ISERegulatorOutput sec = (ISERegulatorOutput) columnNode;
    			ISERegulatorInput pri = sec.getInput();
    			double Vmin = pri.getMinimumInputVoltage();
    			double Vreg = pri.getRegulatedVoltage();
    			double Vmax = pri.getMaximumInputVoltage();
    			double deltaV = pri.getOutputRipple();
    			double res = pri.getOutputResistance();
    			int iPri = unknownVoltageNodes.indexOf(pri);
    			int iSec = columnIndex;
    			
    			double coef;
    			if (voltages[iPri] > Vmin){
    					double ratio = deltaV/(Vmax-Vmin);
    					double c = Vreg - (Vmax+Vmin)*deltaV/(Vmax-Vmin)/2;
    					coef = -ratio/res-c/res/voltages[iPri];
    			}else{
    					double ratio = (Vreg-0.5*deltaV)/Vmin;
            			coef = -ratio / res;
        		}     	
    			
    			matrix.setElementValue(columnIndex, iPri, coef);	//columnIndex = iSec
			}
			else if (columnNode instanceof ISEDiodeInput){
    			ISEDiodeInput input = (ISEDiodeInput) columnNode;
    			ISEDiodeOutput output = input.getOutput();
    			double Vd = input.getVoltageDrop();
    			int iPri = columnIndex;
    			int iSec = unknownVoltageNodes.indexOf(output);	
    			double V = voltages[iPri]-voltages[iSec];
    			
    			double coef;
    			if (V>Vd)
    				coef = -1.0D / input.getForwardResistance();                    			
    			else
            		coef = -1.0D / input.getReverseResistance();
    			
    			matrix.setElementValue(columnIndex, iSec, coef);	//columnIndex = iPri
			}
			else if (columnNode instanceof ISEDiodeOutput){
    			ISEDiodeOutput output = (ISEDiodeOutput) columnNode;
    			ISEDiodeInput input = output.getInput();
    			double Vd = input.getVoltageDrop();
    			int iPri = unknownVoltageNodes.indexOf(input);
    			int iSec = columnIndex;
    			double V = voltages[iPri]-voltages[iSec];

    			double coef;
    			if (V>Vd)
    				coef = -1.0D / input.getForwardResistance();                    			
    			else
            		coef = -1.0D / input.getReverseResistance();
    			
    			matrix.setElementValue(columnIndex, iPri, coef);	//columnIndex = iSec
			}
        	
        	
        	
        	//Add shunt parameters
        	if (columnNode instanceof ISEVoltageSource){
				ISEVoltageSource r = (ISEVoltageSource) columnNode;
				diagonalElement += 1.0D / r.getResistance();   						
			}
			else if (columnNode instanceof ISEConstantPowerLoad){
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
			else if (columnNode instanceof ISETransformerPrimary){
       			ISETransformerPrimary pri = (ISETransformerPrimary) columnNode;
       			ISETransformerSecondary sec = pri.getSecondary();
       			double ratio = pri.getRatio();
       			double res = pri.getResistance();
       			
       			diagonalElement += ratio*ratio/res;
			}
			else if (columnNode instanceof ISETransformerSecondary){
				ISETransformerSecondary sec = (ISETransformerSecondary) columnNode;
				ISETransformerPrimary pri = sec.getPrimary();
       			double ratio = pri.getRatio();
       			double res = pri.getResistance(); 
       			
       			diagonalElement += 1.0D / res;
			}
			else if (columnNode instanceof ISERegulatorInput){
				ISERegulatorInput pri = (ISERegulatorInput) columnNode;
				ISERegulatorOutput sec = pri.getOutput();
    			double Vmin = pri.getMinimumInputVoltage();
    			double Vreg = pri.getRegulatedVoltage();
    			double Vmax = pri.getMaximumInputVoltage();
    			double deltaV = pri.getOutputRipple();
    			double res = pri.getOutputResistance();
    			int iPri = columnIndex;
    			int iSec = unknownVoltageNodes.indexOf(sec);
				
    			
				if (voltages[iPri] > Vmin){
					double ratio = deltaV/(Vmax-Vmin);
					double c = Vreg - (Vmax+Vmin)*deltaV/(Vmax-Vmin)/2;
					diagonalElement += ratio*ratio/res + (c*voltages[iSec]-c*c)/res/voltages[iPri]/voltages[iPri];
				}else{
					double ratio = (Vreg-0.5*deltaV)/Vmin;
					diagonalElement += ratio*ratio/res;
    			} 
			}
			else if (columnNode instanceof ISERegulatorOutput){
				ISERegulatorOutput sec = (ISERegulatorOutput) columnNode;
    			ISERegulatorInput pri = sec.getInput();
    			double res = pri.getOutputResistance();

    			diagonalElement += 1.0D / res;
			}
			else if (columnNode instanceof ISEDiodeInput){
    			ISEDiodeInput input = (ISEDiodeInput) columnNode;
    			ISEDiodeOutput output = input.getOutput();
    			double Vd = input.getVoltageDrop();
    			int iPri = columnIndex;
    			int iSec = unknownVoltageNodes.indexOf(output);	
    			double V = voltages[iPri]-voltages[iSec];
    			
    			if (V>Vd)
    				diagonalElement += 1.0D / input.getForwardResistance();
    			else
    				diagonalElement += 1.0D / input.getReverseResistance();
			}
			else if (columnNode instanceof ISEDiodeOutput){
    			ISEDiodeOutput output = (ISEDiodeOutput) columnNode;
    			ISEDiodeInput input = output.getInput();
    			double Vd = input.getVoltageDrop();
    			int iPri = unknownVoltageNodes.indexOf(input);
    			int iSec = columnIndex;
    			double V = voltages[iPri]-voltages[iSec];

    			if (V>Vd)
    				diagonalElement += 1.0D / input.getForwardResistance();
    			else
    				diagonalElement += 1.0D / input.getReverseResistance();
			} 

        	
        	matrix.setElementValue(columnIndex, columnIndex, diagonalElement);
        }

        matrix.finishEditing();
    }
    
    double norm_inf(double[] A)
    {
    	int i;
    	double t = A[0];
    	double ret = t;

    	if ( t < 0 )
    	{
    		ret = -t;
    	}

    	for ( i = 1; i < A.length; ++i )
    	{
    		t = A[i];
    		if ( t < 0 ) t = -t;
    		if ( ret < t ) ret = t;
    	}

    	return ret;
    }

    public void run(BakaGraph<ISESimulatable> tileEntityGraph) {
        List<ISESimulatable> unknownVoltageNodes = new ArrayList<ISESimulatable>();
        unknownVoltageNodes.clear();
        unknownVoltageNodes.addAll(tileEntityGraph.vertexSet());
    	int matrixSize = unknownVoltageNodes.size();

    	double[] voltages = new double[matrixSize];
    	double[] currents;   
    	

    	int scaler = 1;

    	currents = calcCurrents(voltages, unknownVoltageNodes, tileEntityGraph);
    	
        for (iterations = 0; iterations <= maxIteration; iterations ++) {
        	double max_f = norm_inf(currents);
        	
            formJacobian(voltages, unknownVoltageNodes, tileEntityGraph);

            attemptSolving(currents); 
            //currents is now deltaV
            
            double norm_inf_dx = norm_inf(currents);

            for (int i = 0; i < matrixSize; i++) 
            	voltages[i] += currents[i]/((double)scaler);
        	
            currents = calcCurrents(voltages, unknownVoltageNodes, tileEntityGraph);
            
            double max_f1 = norm_inf(currents);
            
            if (max_f1<max_f){
            	if (norm_inf_dx<epsilon)
            		break;
            	else
            		continue;
            }else{
            	if (max_f1<epsilon)
            		break;
            	else if (iterations>convergenceAssistantTiggerLevel)
            		scaler*=2;
            }
        };
        
    	if (iterations > maxIteration)
    		SEUtils.logInfo("Maximum number of iteration has reached, something must go wrong!");
    	else
    		SEUtils.logInfo("Run!" + String.valueOf(iterations));


        voltageCache.clear();
        for (int i = 0; i < matrixSize; i++) {
            voltageCache.put(unknownVoltageNodes.get(i), voltages[i]);
        }
        
    }

    /*End of Simulator*/
}
