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
import simElectricity.Common.EnergyNet.Components.GridNode;
import simElectricity.Common.EnergyNet.Components.Junction;
import simElectricity.Common.EnergyNet.Components.RegulatorInput;
import simElectricity.Common.EnergyNet.Components.SEComponent;
import simElectricity.API.SEAPI;
import sun.security.ssl.Debug;

import java.util.*;

public final class EnergyNet{	
	Simulator simulator;
	
	//Contains information about the grid
	private EnergyNetDataProvider dataProvider;

    private String matrixSolverName;
    
    private boolean calc = false;
    
    //These tileEntities are called after the energyNet is updated 
    private List<IEnergyNetUpdateHandler> energyNetUpdateAgents = new LinkedList<IEnergyNetUpdateHandler>();


    public String[] info(){
    	SEGraph tileEntityGraph = dataProvider.getTEGraph();
    	String density;

    	if (tileEntityGraph.size() == 0 && dataProvider.getGridObjectCount() == 0){
    		return new String[]{
    				"EnergyNet is empty and idle",
    				"Matrix solving algorithsm: " + matrixSolverName
    		};
    	}

    	if (tileEntityGraph.size() == 0){
    		density = "Undefined";
    	}else{
    		density = String.valueOf(simulator.getTotalNonZeros() * 100 / simulator.getMatrixSize()/ simulator.getMatrixSize()) + "%";
    	}
    	
    	return new String[]{
    	"Loaded entities: " + String.valueOf(tileEntityGraph.size()),
    	"Grid Objects: " + String.valueOf(dataProvider.getGridObjectCount()),
    	"Matrix size: " + String.valueOf(simulator.getMatrixSize()),
    	"Non-zero elements: " + String.valueOf(simulator.getTotalNonZeros()),
    	"Density: " + density,
    	"Matrix solving algorithsm: " + matrixSolverName,
    	"Iterations:" + String.valueOf(simulator.getLastIteration())
    	};
    }

    public void reFresh(){
        calc = true;
    	onTick();
    }




    /**
     * Called in each tick to attempt to do calculation
     */
    public void onTick() {
        //energyNet.calc = true;
        if (calc) {
        	calc = false;
        	
        	SEGraph tileEntityGraph = dataProvider.getTEGraph();
        	//tileEntityGraph.getTerminalNodes();
            simulator.run(tileEntityGraph);
        	
            try {   
	            for (Iterator<IEnergyNetUpdateHandler> iterator = energyNetUpdateAgents.iterator(); iterator.hasNext(); ) {
	            	IEnergyNetUpdateHandler u = iterator.next();
	            	u.onEnergyNetUpdate();
	            }
            } catch (Exception ignored) {
            }
        }
    }

    //Editing of the jGraph--------------------------------------------------------------------------------

    /**
     * Internal use only, return a list containing neighbor TileEntities (Just for IBaseComponent)
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

    /**
     * Add a TileEntity to the energynet
     */
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

    /**
     * Remove a TileEntity from the energy net
     */
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
    
    /**
     * Creation of the energy network
     */
    public EnergyNet(World world) { 
    	//Load configuration
    	matrixSolverName = ConfigManager.matrixSolver;
    	//Create simulator
    	simulator = new Simulator(matrixSolverName, 
    			ConfigManager.maxIteration,
    			Math.pow(10, -ConfigManager.precision),
    			1.0D/ConfigManager.shuntResistance,
    			1.0D/ConfigManager.shuntPN);
    	
    	//Initialize data provider
    	dataProvider = EnergyNetDataProvider.get(world);
    	
        SEUtils.logInfo("EnergyNet has been created for DIM" + String.valueOf(world.provider.dimensionId));
    }
}
