/*
 * Copyright (C) 2014 SimElectricity
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package simElectricity.Common.EnergyNet;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.EnergyTile.*;
import simElectricity.API.Energy;
import simElectricity.API.IEnergyNetUpdateHandler;
import simElectricity.API.Util;
import simElectricity.Common.ConfigManager;
import simElectricity.Common.SEUtils;
import sun.security.ssl.Debug;

import java.util.*;

public final class EnergyNet {
	public Grid grid;
	
    //Records the connection between components
    private BakaGraph tileEntityGraph = new BakaGraph();
    //Stores result, the voltage of every nodes
    private Map<ISESimulatable, Double> voltageCache = new HashMap<ISESimulatable, Double>();
    //Matrix solving algorithm used to solve the problem
    private MatrixResolver matrix = MatrixResolver.MatrixHelper.newResolver(ConfigManager.matrixSolver);
    //Records the number of iterations during last iterating process
    private int iterations;
    //The allowed mismatch
    private double epsilon;
    //The maximum allowed number of iterations
    private int maxIteration;
    
    private boolean calc = false;
    
    //These tileEntities are called after the energyNet is updated 
    private List<IEnergyNetUpdateHandler> energyNetUpdateAgents = new LinkedList<IEnergyNetUpdateHandler>();


    public String[] info(){
    	String[] temp = matrix.toString().split("[.]");

    	if (tileEntityGraph.size() == 0){
    		return new String[]{
    				"EnergyNet is empty and idle",
    				"Matrix solving algorithsm: " + temp[temp.length-1].split("@")[0]
    		};
    	}

    	return new String[]{
    	"Loaded entities: " + String.valueOf(tileEntityGraph.size()),
    	"Non-zero elements: " + String.valueOf(matrix.getTotalNonZeros()),
    	"Sparse rate: " + String.valueOf(matrix.getTotalNonZeros() * 100 / (tileEntityGraph.size() * tileEntityGraph.size())) + "%",
    	"Matrix solving algorithsm: " + temp[temp.length-1].split("@")[0],
    	"Iterations:" + iterations,
    	"Grid Nodes: " + String.valueOf(grid.gridData.gridObjects.size())
    	};
    }

    public void reFresh(){
        calc = true;
    	onTick();
    }

    //Simulator------------------------------------------------------------------------
    private void attemptSolving(double[] b){
        if (!matrix.solve(b)){
        	throw new RuntimeException("Due to incorrect value of components, the energy net has been shutdown!");
        }
    }
    
    private double calcR(ISESimulatable cur, ISESimulatable neighbor){
    	if (cur instanceof ISEConductor){
    		ISEConductor conductor = (ISEConductor) cur;
    		if (neighbor instanceof ISEConductor){
    			ISEConductor conductor2 = (ISEConductor) neighbor;
    			return conductor.getResistance() + conductor2.getResistance();
    		}else if (neighbor instanceof ISEJunction){
    			ISEJunction junction = (ISEJunction) neighbor;
    			return conductor.getResistance() + junction.getResistance(conductor);
    		}else{
    			return conductor.getResistance();
    		}
    	}else if (cur instanceof ISEJunction){
    		ISEJunction junction = (ISEJunction) cur;
    		if (neighbor instanceof ISEConductor){
    			ISEConductor conductor = (ISEConductor) neighbor;
    			return conductor.getResistance() + junction.getResistance(conductor);
    		}else if (neighbor instanceof ISEJunction){
    			ISEJunction junction2 = (ISEJunction) neighbor;
    			return junction.getResistance(junction2) + junction2.getResistance(junction);
    		}else{
    			//return conductor.getResistance();
    		}
    	}
		return epsilon;
    }


    private void runSimulator() {
        List<ISESimulatable> unknownVoltageNodes = new ArrayList<ISESimulatable>();
        unknownVoltageNodes.clear();
        unknownVoltageNodes.addAll(tileEntityGraph.vertexSet());
    	int matrixSize = unknownVoltageNodes.size();
    	double[] Isch = new double[matrixSize]; 
    	double[] voltages = new double[matrixSize];
    	double[] currents = new double[matrixSize];   
    	double[] b = new double[matrixSize];

    	double[] lastV = new double[matrixSize];
    	double[] lastI = new double[matrixSize]; 
    	

    	//Determine Isch and voltage for the first iteration
        for (int nodeIndex = 0; nodeIndex < matrixSize; nodeIndex++) {
        	ISESimulatable curNode = unknownVoltageNodes.get(nodeIndex);
        	//Use flag start to initiate each calculation
        	voltages[nodeIndex] = 0;
        	
        	/*
        	if (voltageCache.containsKey(curNode)){
        		voltages[nodeIndex] = voltageCache.get(curNode); //Use values from previous calculations if possible
        	}else{
        		voltages[nodeIndex] = 0; //Default initial value
        	}
        	*/
        	
        	//Find static current sources on each node
        	if (curNode instanceof ISEVoltageSource){
        		ISEVoltageSource vs = (ISEVoltageSource)curNode;
        		Isch[nodeIndex] = vs.getOutputVoltage() / vs.getResistance();
        	}else{
        		Isch[nodeIndex] = 0;
        	}
        }
    	
        
        iterations = 0;
        while(true) {       	
        	//Calculate the current flow into each node using their voltage
            for (int nodeIndex = 0; nodeIndex < matrixSize; nodeIndex++) {
            	ISESimulatable curNode = unknownVoltageNodes.get(nodeIndex);
             	
            	currents[nodeIndex] = 0;
            	
    			if (curNode instanceof ISEConductor || curNode instanceof ISEJunction){
    	        	for (ISESimulatable neighbor : tileEntityGraph.neighborListOf(curNode)){
    	        		int iNeighbor = unknownVoltageNodes.indexOf(neighbor);
    	        		
    	        		currents[nodeIndex] += (voltages[nodeIndex] - voltages[iNeighbor])/calcR(curNode, neighbor);
    	        	}			
    			}
    			else{
    				//For other components, its neighbor must be a ISEConductor
    				List<ISESimulatable> neighborList = tileEntityGraph.neighborListOf(curNode);
    				ISEConductor conductor = (ISEConductor) (neighborList.isEmpty() ? null : neighborList.get(0));
    				
    				if (conductor != null){
    					int iConductor = unknownVoltageNodes.indexOf(conductor);
    					currents[nodeIndex] += (voltages[nodeIndex] - voltages[iConductor])/conductor.getResistance();		
    				}
    				
    				
    				if (curNode instanceof ISEVoltageSource){
            			ISEVoltageSource vs = (ISEVoltageSource) curNode;
            			currents[nodeIndex] += (voltages[nodeIndex]) / vs.getResistance();
            		}else if (curNode instanceof ISETransformerPrimary){
            			ISETransformerPrimary pri = (ISETransformerPrimary) curNode;
            			ISETransformerSecondary sec = pri.getSecondary();
            			double ratio = pri.getRatio();
            			double res = pri.getResistance();
            			int iSec = unknownVoltageNodes.indexOf(sec);
            			currents[nodeIndex] += (voltages[nodeIndex]*ratio*ratio/res) - (voltages[iSec]*ratio/res);
            		}else if (curNode instanceof ISETransformerSecondary){
            			ISETransformerSecondary sec = (ISETransformerSecondary) curNode;
            			ISETransformerPrimary pri = sec.getPrimary();
            			double ratio = pri.getRatio();
            			double res = pri.getResistance();
            			int iPri = unknownVoltageNodes.indexOf(pri);
            			currents[nodeIndex] += -(voltages[iPri]*ratio/res) + (voltages[nodeIndex]/res);
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
            			
        				
        				currents[nodeIndex] += I;
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
        				
        			
        				currents[nodeIndex] += I;
            		}else if (curNode instanceof ISEDiodeInput){
            			ISEDiodeInput input = (ISEDiodeInput) curNode;
            			ISEDiodeOutput output = input.getOutput();
            			int iPri = nodeIndex;
            			int iSec = unknownVoltageNodes.indexOf(output);	
            			
            			double Vj = input.getVoltageDrop();
            			double Vd = voltages[iPri]-voltages[iSec];
            			double Id;
            			
            			if (Vd>Vj){
            				Id = (Vd-Vj)/input.getForwardResistance() - Vj/input.getForwardResistance() + Vj/input.getReverseResistance();
            			}else{
            				Id = (Vd-Vj)/input.getReverseResistance();
            			}
            			
            			currents[nodeIndex] += Id;
            		}else if (curNode instanceof ISEDiodeOutput){
            			ISEDiodeOutput output = (ISEDiodeOutput) curNode;
            			ISEDiodeInput input = output.getInput();
            			int iPri = unknownVoltageNodes.indexOf(input);
            			int iSec = nodeIndex;
            			
            			double Vj = input.getVoltageDrop();
            			double Vd = voltages[iPri]-voltages[iSec];
            			double Id;
            			
            			if (Vd>Vj){
            				Id = (Vd-Vj)/input.getForwardResistance() - Vj/input.getForwardResistance() + Vj/input.getReverseResistance();
            			}else{
            				Id = (Vd-Vj)/input.getReverseResistance();
            			}
            			
            			currents[nodeIndex] -= Id;
            		}
            		else if (curNode instanceof ISEConstantPowerLoad){
            			ISEConstantPowerLoad load = (ISEConstantPowerLoad)curNode;
            			
            			double V = voltages[unknownVoltageNodes.indexOf(curNode)];
            			double Rcal = V*V/load.getRatedPower();
            			
            			if (Rcal > load.getMaximumResistance())
            				Rcal = load.getMaximumResistance();
            			if (Rcal < load.getMinimumResistance())
            				Rcal = load.getMinimumResistance();
            			
            			if (load.isEnabled())
            				currents[nodeIndex] += V/Rcal;
            		}
    			}
    			
    			b[nodeIndex] = Isch[nodeIndex] - currents[nodeIndex]; //Current mismatch
            }

            
            
            
        	boolean keepGoing = false;
         
        	if (iterations == 0){
        		keepGoing = true;
        		
        		for (int i = 0; i < matrixSize; i++) {
        			lastI[i] = currents[i];
        		}
        	}else{
                for (int i = 0; i < matrixSize; i++) {
                	if (Math.abs(voltages[i]-lastV[i]) > epsilon)
                		keepGoing = true;
                	
                	if (Math.abs(currents[i]-lastI[i]) > epsilon)
                		keepGoing = true;
                	
                	lastI[i] = currents[i];
                }      		
        	}

            
            if (keepGoing){
            	if (iterations > maxIteration){
            		SEUtils.logInfo("Maximum number of iteration has reached, something must go wrong!");
            		break;
            	}
            }else{
            	break;
            }   
            

            matrix.newMatrix(matrixSize);
            //Form Jacobian     
            for (int columnIndex = 0; columnIndex < matrixSize; columnIndex++) {
            	ISESimulatable columnNode = unknownVoltageNodes.get(columnIndex);
            	List<ISESimulatable> neighborList = tileEntityGraph.neighborListOf(columnNode);
            	for (int rowIndex = 0; rowIndex < matrixSize; rowIndex++) {
            		ISESimulatable rowNode = unknownVoltageNodes.get(rowIndex);
            		double coef = 0.0D;
            		
        			if (columnNode instanceof ISEConductor || columnNode instanceof ISEJunction){
        				if (columnIndex == rowIndex) {
        					//Diagonal elements
        					for (ISESimulatable neighbor : neighborList) {
        						coef += 1.0D / calcR(columnNode, neighbor);
        					}
        				}else if (neighborList.contains(rowNode)){
        					//Off-Diagonal elements
        					coef = -1.0D / calcR(columnNode, rowNode);  						
        				}
        			}else{
        				//Other components
        				ISEConductor neighborConductor = (ISEConductor) (neighborList.isEmpty() ? null : neighborList.get(0));
        				if (columnIndex == rowIndex) {
        					//Diagonal elements
        					if (neighborConductor != null)	coef += 1.0D / neighborConductor.getResistance();
        					
        					if (columnNode instanceof ISEVoltageSource){
        						ISEVoltageSource r = (ISEVoltageSource) columnNode;
        						coef += 1.0D / r.getResistance();   						
        					}
        					else if (columnNode instanceof ISETransformerPrimary){
            	       			ISETransformerPrimary pri = (ISETransformerPrimary) columnNode;
            	       			ISETransformerSecondary sec = pri.getSecondary();
            	       			double ratio = pri.getRatio();
            	       			double res = pri.getResistance();
            	       			
            	       			coef += ratio*ratio/res;
        					}
        					else if (columnNode instanceof ISETransformerSecondary){
            					ISETransformerSecondary sec = (ISETransformerSecondary) columnNode;
            					ISETransformerPrimary pri = sec.getPrimary();
            	       			double ratio = pri.getRatio();
            	       			double res = pri.getResistance(); 
            	       			
            	       			coef += 1.0D / res;
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
                					coef += ratio*ratio/res + (c*voltages[iSec]-c*c)/res/voltages[iPri]/voltages[iPri];
                				}else{
                					double ratio = (Vreg-0.5*deltaV)/Vmin;
                					coef += ratio*ratio/res;
                    			} 
        					}
        					else if (columnNode instanceof ISERegulatorOutput){
        						ISERegulatorOutput sec = (ISERegulatorOutput) columnNode;
        	        			ISERegulatorInput pri = sec.getInput();
        	        			double res = pri.getOutputResistance();

        	        			coef += 1.0D / res;
        					}
        					else if (columnNode instanceof ISEDiodeInput){
                    			ISEDiodeInput input = (ISEDiodeInput) columnNode;
                    			ISEDiodeOutput output = input.getOutput();
                    			double Vd = input.getVoltageDrop();
                    			int iPri = columnIndex;
                    			int iSec = unknownVoltageNodes.indexOf(output);	
                    			double V = voltages[iPri]-voltages[iSec];
                    			
                    			if (V>Vd)
                    				coef += 1.0D / input.getForwardResistance();
                    			else
                    				coef += 1.0D / input.getReverseResistance();
        					}
        					else if (columnNode instanceof ISEDiodeOutput){
                    			ISEDiodeOutput output = (ISEDiodeOutput) columnNode;
                    			ISEDiodeInput input = output.getInput();
                    			double Vd = input.getVoltageDrop();
                    			int iPri = unknownVoltageNodes.indexOf(input);
                    			int iSec = columnIndex;
                    			double V = voltages[iPri]-voltages[iSec];

                    			if (V>Vd)
                    				coef += 1.0D / input.getForwardResistance();
                    			else
                    				coef += 1.0D / input.getReverseResistance();
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
                    				coef += 1.0D / Rcal;
        					}
        				}else{ 
        					//Off-Diagonal elements
        					if (neighborConductor == unknownVoltageNodes.get(rowIndex)){
	        					coef = -1.0D / neighborConductor.getResistance();
	        				}else{
	        					if (columnNode instanceof ISETransformerPrimary){
	            	       			ISETransformerPrimary pri = (ISETransformerPrimary) columnNode;
	            	       			ISETransformerSecondary sec = pri.getSecondary();
	            	       			
	            	       			if (rowNode == sec){
	            	       				coef = -pri.getRatio() / pri.getResistance();
	            	       			}	
	            				}
	        					else if (columnNode instanceof ISETransformerSecondary){
	            					ISETransformerSecondary sec = (ISETransformerSecondary) columnNode;
	            					ISETransformerPrimary pri = sec.getPrimary();
	            	       			
	            	       			if (rowNode == pri){
	            	       				coef = -pri.getRatio() / pri.getResistance();
	            	       			}
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
	                    			
	                    			if (rowNode == sec){
	                    				if (voltages[iPri] > Vmin){
	                    					double ratio = deltaV/(Vmax-Vmin);
	                    					double c = Vreg - (Vmax+Vmin)*deltaV/(Vmax-Vmin)/2;
	                    					coef = -ratio / res;
	                    				}else{
	                    					double ratio = (Vreg-0.5*deltaV)/Vmin;
	                    					coef = -ratio / res;
	                        			} 
	                    			}
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
	                    			
	                    			if (rowNode == pri){
	                    				if (voltages[iPri] > Vmin){
	                    					double ratio = deltaV/(Vmax-Vmin);
	                    					double c = Vreg - (Vmax+Vmin)*deltaV/(Vmax-Vmin)/2;
	                    					coef = -ratio/res-c/res/voltages[iPri];
	                    				}else{
	                    					double ratio = (Vreg-0.5*deltaV)/Vmin;
		                        			coef = -ratio / res;
	                        			}     	
	                    			}
	            				}
	        					else if (columnNode instanceof ISEDiodeInput){
	                    			ISEDiodeInput input = (ISEDiodeInput) columnNode;
	                    			ISEDiodeOutput output = input.getOutput();
	                    			double Vd = input.getVoltageDrop();
	                    			int iPri = columnIndex;
	                    			int iSec = unknownVoltageNodes.indexOf(output);	
	                    			double V = voltages[iPri]-voltages[iSec];
	                    			
	                    			if (rowNode == output){
	                    				if (V>Vd)
	                    					coef = -1.0D / input.getForwardResistance();                    			
	                    				else
	                            			coef = -1.0D / input.getReverseResistance();
	                    			}
	        					}
	        					else if (columnNode instanceof ISEDiodeOutput){
	                    			ISEDiodeOutput output = (ISEDiodeOutput) columnNode;
	                    			ISEDiodeInput input = output.getInput();
	                    			double Vd = input.getVoltageDrop();
	                    			int iPri = unknownVoltageNodes.indexOf(input);
	                    			int iSec = columnIndex;
	                    			double V = voltages[iPri]-voltages[iSec];

	                    			if (rowNode == input){
	                    				if (V>Vd)
	                    					coef = -1.0D / input.getForwardResistance();                    			
	                    				else
	                            			coef = -1.0D / input.getReverseResistance();
	                    			}
	        					}
	        				}
        				}
        			}
            		matrix.pushCoefficient(coef);
            	}
                matrix.pushColumn();
            }

            matrix.finalizeLHS();
            attemptSolving(b); //b is now deltaV
            
            
            Debug.println("Iteration:", String.valueOf(iterations));
            for (int i = 0; i < matrixSize; i++) {
            	if (!Double.isNaN(b[i])){
            		lastV[i] = voltages[i];
            		voltages[i] += b[i];
            	}
            	String[] temp = unknownVoltageNodes.get(i).toString().split("[.]");
            	Debug.println(temp[temp.length-1].split("@")[0], String.valueOf(voltages[i]));
            }
      
            iterations++;
        };
        
        


        voltageCache.clear();
        for (int i = 0; i < matrixSize; i++) {
            voltageCache.put(unknownVoltageNodes.get(i), voltages[i]);
        }
        
        System.out.println("Run!" + String.valueOf(iterations));
    }

    /*End of Simulator*/


    /**
     * Called in each tick to attempt to do calculation
     */
    public void onTick() {
        //energyNet.calc = true;
        if (calc) {
            runSimulator();

            for (IEnergyNetUpdateHandler u : energyNetUpdateAgents)
            	u.onEnergyNetUpdate();
            
            calc = false;
        }
    }

    //Editing of the jGraph--------------------------------------------------------------------------------

    /**
     * Internal use only, return a list containing neighbor TileEntities (Just for IBaseComponent)
     */
    private List<ISESimulatable> neighborListOfConductor(TileEntity te) {
        List<ISESimulatable> result = new ArrayList<ISESimulatable>();
        TileEntity neighborTE;
        
        if (te instanceof ISEConductor) {
        	ISEConductor wire = (ISEConductor) te;
            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            	neighborTE = Util.getTileEntityonDirection(te, direction);
                if (neighborTE instanceof ISEConductor) {  //Conductor
                	ISEConductor neighbor = (ISEConductor) neighborTE;

                    if (wire.getColor() == 0 ||
                            neighbor.getColor() == 0 ||
                            wire.getColor() == neighbor.getColor()) {
                        result.add(neighbor);
                    }
                }
                else if (neighborTE instanceof ISETile) {
                	ISETile tile = (ISETile)neighborTE;
                	ISESubComponent component = tile.getComponent(direction.getOpposite());
                	
                	if (component != null){
                		result.add(component);
                	}
                }
                else if (neighborTE instanceof ISESimpleTile) {
                	ISESimpleTile tile = (ISESimpleTile)neighborTE;
                	
                	if (tile.getFunctionalSide() == direction.getOpposite()){
                		result.add(tile);
                	}
                }
            }
        }

        return result;
    }

    /**
     * Add a TileEntity to the energynet
     */
    public void addTileEntity(TileEntity te) {
        Map<ISESimulatable, ISESimulatable> neighborMap = new HashMap<ISESimulatable, ISESimulatable>();


        if (te instanceof ISEConductor){
        	tileEntityGraph.addVertex((ISEConductor)te);
        	List<ISESimulatable> neighborList = neighborListOfConductor(te);
            for (ISESimulatable neighbor : neighborList)
                neighborMap.put(neighbor, (ISESimulatable) te);
        }
        else if (te instanceof ISETile){
        	ISETile tile = (ISETile)te;
        	for (ForgeDirection direction : tile.getValidDirections()) {
        		ISESubComponent subComponent = tile.getComponent(direction);
        		
        		if (subComponent instanceof ISEJunction){
        			ISEJunction junction = (ISEJunction) subComponent;
        			List<ISESimulatable> neighborList = new LinkedList<ISESimulatable>();
        			junction.getNeighbors(neighborList);
        			
        			for (ISESimulatable neighbor : neighborList)
        				neighborMap.put(neighbor, subComponent);
        		}else{
                    TileEntity neighbor = Util.getTileEntityonDirection(te, direction);
                    
                    tileEntityGraph.addVertex(subComponent);
                        
                    if (neighbor instanceof ISEConductor)  // Connected properly
                    	neighborMap.put((ISEConductor) neighbor, subComponent);        			
        		}
        	}
        }
        else if (te instanceof ISESimpleTile){
        	ISESimpleTile tile = (ISESimpleTile)te;
        	tileEntityGraph.addVertex(tile);
        	
        	TileEntity neighbor = Util.getTileEntityonDirection(te, tile.getFunctionalSide());
        	
            if (neighbor instanceof ISEConductor)  // Connected properly
            	neighborMap.put((ISEConductor) neighbor, tile);    
        }
        else{
        	//Error
        }
        

        for (ISESimulatable neighbor : neighborMap.keySet()) {
            tileEntityGraph.addVertex(neighborMap.get(neighbor));
            tileEntityGraph.addEdge(neighbor, neighborMap.get(neighbor));
        }
        
        if (te instanceof IEnergyNetUpdateHandler)
        	energyNetUpdateAgents.add((IEnergyNetUpdateHandler)te);

        calc = true;
    }

    /**
     * Remove a TileEntity from the energy net
     */
    public void removeTileEntity(TileEntity te) {
        if (te instanceof ISEConductor) {
        	tileEntityGraph.removeVertex((ISEConductor) te);
        }
	    else if (te instanceof ISETile){
        	ISETile tile = (ISETile)te;
        	for (ForgeDirection direction : tile.getValidDirections()) {
        		ISESubComponent subComponent = tile.getComponent(direction);
        		tileEntityGraph.removeVertex(subComponent);
        	}
	    }else if (te instanceof ISESimpleTile){
	    	tileEntityGraph.removeVertex((ISESimpleTile) te);
	    }
        
        if (te instanceof IEnergyNetUpdateHandler)
        	energyNetUpdateAgents.remove((IEnergyNetUpdateHandler)te);
        calc = true;
    }

    /**
     * Refresh a node information for a tile which ALREADY attached to the energy network
     */
    public void rejoinTileEntity(TileEntity te) {
        removeTileEntity(te);
        addTileEntity(te);
    }

    /**
     * Mark the energy net for updating in next tick
     */
    public void markForUpdate(TileEntity te) {
        calc = true;
    }

    /**
     * Creation of the energy network
     */
    public EnergyNet(World world) {
    	grid = new Grid(world);
    	
    	
    	this.maxIteration = ConfigManager.maxIteration;
    	this.epsilon = Math.pow(10, -ConfigManager.precision);
        SEUtils.logInfo("EnergyNet has been created for DIM" + String.valueOf(world.provider.dimensionId));
    }

    public double getVoltage(ISESimulatable Tile){
         if (voltageCache.containsKey(Tile)){
        	double voltage = voltageCache.get(Tile);
        	if (!Double.isNaN(voltage))	return voltage;
         }
         return 0;
    }

    /**
     * Calculate the voltage of a given EnergyTile RELATIVE TO GROUND!
     */
    public static double getVoltage(ISESimulatable Tile, World world) {
        return WorldData.getEnergyNetForWorld(world).getVoltage(Tile);
    }
}
