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
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.EnergyTile.ISESubComponent;
import simElectricity.API.Tile.ISECableTile;
import simElectricity.API.Tile.ISETile;
import simElectricity.API.IEnergyNetUpdateHandler;
import simElectricity.Common.ConfigManager;
import simElectricity.Common.SEUtils;
import simElectricity.Common.EnergyNet.Components.ConstantPowerLoad;
import simElectricity.Common.EnergyNet.Components.DiodeInput;
import simElectricity.Common.EnergyNet.Components.DiodeOutput;
import simElectricity.Common.EnergyNet.Components.GridNode;
import simElectricity.Common.EnergyNet.Components.Junction;
import simElectricity.Common.EnergyNet.Components.RegulatorController;
import simElectricity.Common.EnergyNet.Components.RegulatorInput;
import simElectricity.Common.EnergyNet.Components.RegulatorOutput;
import simElectricity.Common.EnergyNet.Components.SEComponent;
import simElectricity.Common.EnergyNet.Components.TransformerPrimary;
import simElectricity.Common.EnergyNet.Components.TransformerSecondary;
import simElectricity.Common.EnergyNet.Components.VoltageSource;
import simElectricity.API.SEAPI;
import sun.security.ssl.Debug;

import java.util.*;

public final class EnergyNet{		
	//Contains information about the grid
	private EnergyNetDataProvider dataProvider;
    
    private boolean calc = false;
    
    //These tileEntities are called after the energyNet is updated 
    private List<IEnergyNetUpdateHandler> energyNetUpdateAgents = new LinkedList<IEnergyNetUpdateHandler>();

    public EnergyNet(World world) { 
    	//Initialize simulator
    	maxIteration = ConfigManager.maxIteration;
    	epsilon = Math.pow(10, -ConfigManager.precision);
    	Gpn = 1.0D/ConfigManager.shuntPN;
    	matrix = IMatrixResolver.MatrixHelper.newResolver(ConfigManager.matrixSolver);
    	
    	//Initialize data provider
    	dataProvider = EnergyNetDataProvider.get(world);
    	
        SEUtils.logInfo("EnergyNet has been created for DIM" + String.valueOf(world.provider.dimensionId));
    }
    
    public String[] info(){
    	SEGraph tileEntityGraph = dataProvider.getTEGraph();
    	String density;

    	if (tileEntityGraph.size() == 0 && dataProvider.getGridObjectCount() == 0){
    		return new String[]{
    				"EnergyNet is empty and idle",
    				"Matrix solving algorithsm: " + ConfigManager.matrixSolver
    		};
    	}

    	if (tileEntityGraph.size() == 0){
    		density = "Undefined";
    	}else{
    		density = String.valueOf(matrix.getTotalNonZeros() * 100 / matrix.getMatrixSize()/ matrix.getMatrixSize()) + "%";
    	}
    	
    	return new String[]{
    	"Loaded entities: " + String.valueOf(tileEntityGraph.size()),
    	"Grid Objects: " + String.valueOf(dataProvider.getGridObjectCount()),
    	"Matrix size: " + String.valueOf(matrix.getMatrixSize()),
    	"Non-zero elements: " + String.valueOf(matrix.getTotalNonZeros()),
    	"Density: " + density,
    	"Matrix solving algorithsm: " + ConfigManager.matrixSolver,
    	"Iterations:" + String.valueOf(iterations)
    	};
    }

    public void reFresh(){
        calc = true;
    	onTick();
    }

    public void onTick() {
        //energyNet.calc = true;
        if (calc) {
        	calc = false;
        	
            runSimulator();
        	
            try {   
	            for (Iterator<IEnergyNetUpdateHandler> iterator = energyNetUpdateAgents.iterator(); iterator.hasNext(); ) {
	            	IEnergyNetUpdateHandler u = iterator.next();
	            	u.onEnergyNetUpdate();
	            }
            } catch (Exception ignored) {
            }
        }
    }


    
    
    
    
    
    
    
    /**
     * Internal use only
     */
    private List<ISESimulatable> neighborListOfConductor(TileEntity te) {
        List<ISESimulatable> result = new ArrayList<ISESimulatable>();
        TileEntity neighborTE;
        
        if (te instanceof ISECableTile) {
        	ISECableTile cableTile = (ISECableTile) te;
            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            	neighborTE = SEAPI.utils.getTileEntityonDirection(te, direction);
                if (neighborTE instanceof ISECableTile) {  //Conductor
                	ISECableTile neighbor = (ISECableTile) neighborTE;

                    if (cableTile.getColor() == 0 ||
                            neighbor.getColor() == 0 ||
                            cableTile.getColor() == neighbor.getColor()) {
                        result.add(neighbor.getNode());
                    }
                }
                else if (neighborTE instanceof ISETile) {
                	ISETile tile = (ISETile)neighborTE;
                	ISESubComponent component = tile.getComponent(direction.getOpposite());
                	
                	if (component != null){
                		result.add(component);
                	}
                }
            }
        }

        return result;
    }


    //EnergyNet event handlers
    public void addTileEntity(TileEntity te) {
        //Map<ISESimulatable, ISESimulatable> neighborMap = new HashMap<ISESimulatable, ISESimulatable>();
    	SEGraph tileEntityGraph = dataProvider.getTEGraph();

    	
    	
        if (te instanceof ISECableTile){
        	tileEntityGraph.addVertex((SEComponent) ((ISECableTile)te).getNode());
        	List<ISESimulatable> neighborList = neighborListOfConductor(te);
            for (ISESimulatable neighbor : neighborList)
            	tileEntityGraph.addEdge((SEComponent) neighbor, (SEComponent) ((ISECableTile)te).getNode());
        }
        else if (te instanceof ISETile){
        	ISETile tile = (ISETile)te;
        	for (ForgeDirection direction : tile.getValidDirections()) {
        		ISESubComponent subComponent = tile.getComponent(direction);
        		tileEntityGraph.addVertex((SEComponent) subComponent);
        		
        		if (subComponent instanceof Junction){
        			Junction junction = (Junction) subComponent;
        			List<ISESimulatable> neighborList = new LinkedList<ISESimulatable>();
        			junction.data.getNeighbors(neighborList);
        			
        			for (ISESimulatable neighbor : neighborList)
        				tileEntityGraph.addEdge((SEComponent) neighbor,(SEComponent)  subComponent);
        		}else{
                    TileEntity neighbor = SEAPI.utils.getTileEntityonDirection(te, direction);
                    
                    if (neighbor instanceof ISECableTile)  // Connected properly
                    	tileEntityGraph.addEdge((SEComponent) ((ISECableTile)neighbor).getNode(),(SEComponent)  subComponent);
                    
                    
                    //Also don`t forget to attach the regulator controller to the energyNet!
                    if (subComponent instanceof RegulatorInput)
                    	tileEntityGraph.addVertex((SEComponent) ((RegulatorInput)subComponent).controller);
        		}
        	}
        }
        else{
        	//Error
        }
        
        
        if (te instanceof IEnergyNetUpdateHandler)
        	energyNetUpdateAgents.add((IEnergyNetUpdateHandler)te);

        calc = true;
    }

    public void removeTileEntity(TileEntity te) {
    	SEGraph tileEntityGraph = dataProvider.getTEGraph();
    	
        if (te instanceof ISECableTile) {
        	tileEntityGraph.removeVertex((SEComponent) ((ISECableTile) te).getNode());
        }
	    else if (te instanceof ISETile){
        	ISETile tile = (ISETile)te;
        	for (ForgeDirection direction : tile.getValidDirections()) {
        		ISESubComponent subComponent = tile.getComponent(direction);
        		tileEntityGraph.removeVertex((SEComponent) subComponent);
        		
        		if (subComponent instanceof RegulatorInput)
                	tileEntityGraph.removeVertex((SEComponent) ((RegulatorInput)subComponent).controller);
        	}
	    }
        
        if (te instanceof IEnergyNetUpdateHandler)
        	energyNetUpdateAgents.remove((IEnergyNetUpdateHandler)te);
        calc = true;
    }

    public void rejoinTileEntity(TileEntity te) {
        removeTileEntity(te);
        addTileEntity(te);
    }

    public void markForUpdate(TileEntity te) {
        calc = true;
    }
    
    public boolean addGridNode(int x, int y, int z, byte type){
    	calc = true;
    	return dataProvider.addGridNode(x, y, z, type) != null;
    }
    
    public boolean removeGridNode(int x, int y, int z){
    	GridNode node = dataProvider.getGridObjectAtCoord(x, y, z);
    	if (node == null)
    		return false;
    	dataProvider.removeGridNode(node);
    	calc = true;
    	return true;
    }
    
    public boolean addGridConnection(int x1, int y1, int z1, int x2, int y2, int z2, double resistance){
    	GridNode node1 = dataProvider.getGridObjectAtCoord(x1,y1,z1);
    	GridNode node2 = dataProvider.getGridObjectAtCoord(x2,y2,z2);
    	
    	if (node1 != null && node2 != null){
    		dataProvider.addGridConnection(node1, node2, resistance);
    		calc = true;
    		return true;
    	}
    	return false;	
    }
    
    public boolean removeGridConnection(int x1, int y1, int z1, int x2, int y2, int z2){
    	GridNode node1 = dataProvider.getGridObjectAtCoord(x1,y1,z1);
    	GridNode node2 = dataProvider.getGridObjectAtCoord(x2,y2,z2);
    	
    	if (node1 != null && node2 != null){
    		dataProvider.removeGridConnection(node1, node2);
    		calc = true;
    		return true;
    	}
    	return false;	
    }
    //End of event handlers
    
    
    
    
    



    //Beginning of simulator
    //Stores result, the voltage of nodes
    private Map<ISESimulatable, Double> voltageCache = new HashMap<ISESimulatable, Double>();
    //Matrix solving algorithm used to solve the problem
    private IMatrixResolver matrix;
    //Records the number of iterations during last iterating process
    private int iterations;
    //The allowed mismatch
    private double epsilon;
    //The maximum allowed number of iterations
    private int maxIteration;
    
    
    
    //The conductance placed between each PN junction(to alleviate convergence problem)
    private double Gpn;
    //Diode parameter for regulator controllers
    private double Vt = 26e-6;
    private double Is = 1e-6;

    public double getVoltage(ISESimulatable Tile){   	
        if (voltageCache.containsKey(Tile)){
	       	double voltage = voltageCache.get(Tile);
	       	if (!Double.isNaN(voltage))	
	       		return voltage;
        }else{
        	SEGraph graph = dataProvider.getTEGraph();
        	//Only apply to cable and transmission lines which have been optimized by the energyNet
        	SEComponent[] terminals = graph.getTerminals((SEComponent) Tile);
        	if (terminals == null)
        		return 0;
        		
        	if (terminals.length == 0)
        		return 0;
        	else if (terminals.length == 1)
        		return getVoltage(terminals[0]);
        	else{
        		double Va = getVoltage(terminals[0]);
        		double Vb = getVoltage(terminals[1]);
        		return Va - (Va-Vb)*graph.R0/(graph.R0+graph.R1);
        	}
        }
        return 0;
   }
    
    public double getCurrentMagnitude(ISESimulatable Tile){
    	SEGraph graph = dataProvider.getTEGraph();
    	if (Tile instanceof Junction){
    		Junction junction = (Junction)Tile;
    		if (junction.neighbors.size() < 2)
    			return 0;
    		if (junction.neighbors.size() > 2)
    			return Double.NaN;
    		
    		double Va = getVoltage(junction);
    		double Vb = getVoltage(junction.optimizedNeighbors.getFirst());
    		return Math.abs((Va-Vb)/(junction.optimizedResistance.getFirst()));
    	}else{
    		//Cable or transmission line
        	SEComponent seTile = (SEComponent) Tile;
        	if (seTile.neighbors.size() < 2)
        		return 0;
        	else if (seTile.neighbors.size() > 2)
        		return Double.NaN;

        	SEComponent[] terminals = graph.getTerminals((SEComponent) Tile);
        	if (terminals.length < 2)
        		return 0;
        	else{
        		double Va = getVoltage(terminals[0]);
        		double Vb = getVoltage(terminals[1]);
        		return Math.abs((Va-Vb)/(graph.R0+graph.R1));
        	}
    	}
    }
   
    //Simulator------------------------------------------------------------------------   
    double[] calcCurrents(double[] voltages, List<SEComponent> unknownVoltageNodes){
    	int matrixSize = unknownVoltageNodes.size();
    	
    	double[] currents = new double[matrixSize];   
    	
    	//Calculate the current flow into each node using their voltage
        for (int nodeIndex = 0; nodeIndex < matrixSize; nodeIndex++) {
        	SEComponent curNode = unknownVoltageNodes.get(nodeIndex);
         	
        	//currents[nodeIndex] = -voltages[nodeIndex]*Gnode;
            
        	//Node - Node
			Iterator<SEComponent> iteratorON = curNode.optimizedNeighbors.iterator();
			Iterator<Double> iteratorR = curNode.optimizedResistance.iterator();
			while (iteratorON.hasNext()){
				SEComponent neighbor = iteratorON.next();
        		int iNeighbor = unknownVoltageNodes.indexOf(neighbor);
        		double R = iteratorR.next();
        		currents[nodeIndex] -= (voltages[nodeIndex] - voltages[iNeighbor])/R;					
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
    			double Dmax = input.data.getDMax();
    			double Rdummy = input.data.getRDummyLoad();
    			
    			double Io = -Vi*(Vc+Dmax)/Ro + Vo/Ro + Vo/Rdummy;
				
				currents[nodeIndex] -= Io;
    		}else if (curNode instanceof RegulatorController){
    			RegulatorController controller = (RegulatorController) curNode;
    			RegulatorInput input = controller.input;
    			RegulatorOutput output = input.output;
    			
    			double Vo = voltages[unknownVoltageNodes.indexOf(output)];	
    			double Vc = voltages[nodeIndex];
    			double A = input.data.getGain();
    			double Rs = input.data.getRs();
    			double Rc = input.data.getRc();
    			double Dmax = input.data.getDMax();
    			
    			
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
    			double Vt = input.data.getThermalVoltage();
    			double Is = input.data.getSaturationCurrent();
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
    			double Vt = input.data.getThermalVoltage();
    			double Is = input.data.getSaturationCurrent();
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

    void formJacobian(double[] voltages, List<SEComponent> unknownVoltageNodes){
    	int matrixSize = unknownVoltageNodes.size();
    	
    	matrix.newMatrix(matrixSize);

        for (int columnIndex = 0; columnIndex < matrixSize; columnIndex++) {
        	SEComponent columnNode = unknownVoltageNodes.get(columnIndex);
        
        	double diagonalElement = 0;
        	
        	//Add conductance between nodes
			Iterator<SEComponent> iteratorON = columnNode.optimizedNeighbors.iterator();
			Iterator<Double> iteratorR = columnNode.optimizedResistance.iterator();
			while (iteratorON.hasNext()){
				SEComponent neighbor = iteratorON.next();
				int rowIndex = unknownVoltageNodes.indexOf(neighbor);
        		double R = iteratorR.next();	
        		
        		diagonalElement += 1.0D / R;
        		
        		matrix.setElementValue(columnIndex, rowIndex, -1.0D / R);
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
    			
    			double Vt = input.data.getThermalVoltage();
    			double Is = input.data.getSaturationCurrent();
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
    			
    			double Vt = input.data.getThermalVoltage();
    			double Is = input.data.getSaturationCurrent();
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
    			diagonalElement += 1.0D / ((RegulatorOutput) columnNode).input.data.getRDummyLoad();
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
    
    public void runSimulator() {
        List<SEComponent> unknownVoltageNodes = dataProvider.getTEGraph().getTerminalNodes();
    	int matrixSize = unknownVoltageNodes.size();

    	double[] voltages = new double[matrixSize];
    	double[] currents;
    	   		
        iterations = 0;
        while(true) {
        	//Calculate the current flow into each node using their voltage
        	currents = calcCurrents(voltages, unknownVoltageNodes);	//Current mismatch
        	
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
        	
        	formJacobian(voltages, unknownVoltageNodes);
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

        voltageCache.clear();
        for (int i = 0; i < matrixSize; i++) {
            voltageCache.put(unknownVoltageNodes.get(i), voltages[i]);
        }
        
        System.out.println("Run!" + String.valueOf(iterations));        
    }

    /*End of Simulator*/

}
