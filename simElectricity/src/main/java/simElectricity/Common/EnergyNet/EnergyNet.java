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
	Simulator simulator;
	
	//Contains information about the grid
	private EnergyNetDataProvider dataProvider;
    
    //private BakaGraph<ISESimulatable> tileEntityGraph;

    private String matrixSolverName;
    
    private boolean calc = false;
    
    //These tileEntities are called after the energyNet is updated 
    private List<IEnergyNetUpdateHandler> energyNetUpdateAgents = new LinkedList<IEnergyNetUpdateHandler>();


    public String[] info(){
    	BakaGraph<ISESimulatable> tileEntityGraph = dataProvider.getTEGraph();
    	String sparseRate;

    	if (tileEntityGraph.size() == 0 && dataProvider.getGridObjectCount() == 0){
    		return new String[]{
    				"EnergyNet is empty and idle",
    				"Matrix solving algorithsm: " + matrixSolverName
    		};
    	}

    	if (tileEntityGraph.size() == 0){
    		sparseRate = "Undefined";
    	}else{
    		sparseRate = String.valueOf(simulator.getTotalNonZeros() * 100 / (tileEntityGraph.size() * tileEntityGraph.size())) + "%";
    	}
    	
    	return new String[]{
    	"Loaded entities: " + String.valueOf(tileEntityGraph.size()),
    	"Grid Objects: " + String.valueOf(dataProvider.getGridObjectCount()),
    	"Non-zero elements: " + String.valueOf(simulator.getTotalNonZeros()),
    	"Sparse rate: " + sparseRate,
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
            //simulator.run(this.tileEntityGraph);
        	BakaGraph<ISESimulatable> tileEntityGraph = dataProvider.getTEGraph();
            try {   
	            for (Iterator<IEnergyNetUpdateHandler> iterator = energyNetUpdateAgents.iterator(); iterator.hasNext(); ) {
	            	IEnergyNetUpdateHandler u = iterator.next();
	            	u.onEnergyNetUpdate();
	            }
            } catch (Exception ignored) {
            }
            

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
        //Map<ISESimulatable, ISESimulatable> neighborMap = new HashMap<ISESimulatable, ISESimulatable>();
    	BakaGraph<ISESimulatable> tileEntityGraph = dataProvider.getTEGraph();

        if (te instanceof ISEConductor){
        	tileEntityGraph.addVertex((ISEConductor)te);
        	List<ISESimulatable> neighborList = neighborListOfConductor(te);
            for (ISESimulatable neighbor : neighborList)
            	tileEntityGraph.addEdge(neighbor, (ISESimulatable) te);
        }
        else if (te instanceof ISETile){
        	ISETile tile = (ISETile)te;
        	for (ForgeDirection direction : tile.getValidDirections()) {
        		ISESubComponent subComponent = tile.getComponent(direction);
        		tileEntityGraph.addVertex(subComponent);
        		
        		if (subComponent instanceof ISEJunction){
        			ISEJunction junction = (ISEJunction) subComponent;
        			List<ISESimulatable> neighborList = new LinkedList<ISESimulatable>();
        			junction.getNeighbors(neighborList);
        			
        			for (ISESimulatable neighbor : neighborList)
        				tileEntityGraph.addEdge(neighbor, subComponent);
        		}else{
                    TileEntity neighbor = Util.getTileEntityonDirection(te, direction);
                        
                    if (neighbor instanceof ISEConductor)  // Connected properly
                    	tileEntityGraph.addEdge((ISEConductor)neighbor, subComponent);
        		}
        	}
        }
        else if (te instanceof ISESimpleTile){
        	ISESimpleTile tile = (ISESimpleTile)te;
        	tileEntityGraph.addVertex(tile);
        	
        	TileEntity neighbor = Util.getTileEntityonDirection(te, tile.getFunctionalSide());
        	
            if (neighbor instanceof ISEConductor)  // Connected properly
            	tileEntityGraph.addEdge((ISEConductor) neighbor, tile);    
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
    	BakaGraph<ISESimulatable> tileEntityGraph = dataProvider.getTEGraph();
    	
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
    	//Load configuration
    	matrixSolverName = ConfigManager.matrixSolver;
    	//Create simulator
    	simulator = new Simulator(matrixSolverName, ConfigManager.maxIteration, Math.pow(10, -ConfigManager.precision));
    	
    	//tileEntityGraph = new BakaGraph<ISESimulatable>();
    	
    	//Init. data provider
    	dataProvider = EnergyNetDataProvider.get(world);
    	
        SEUtils.logInfo("EnergyNet has been created for DIM" + String.valueOf(world.provider.dimensionId));
    }


    /**
     * Calculate the voltage of a given EnergyTile RELATIVE TO GROUND!
     */
    public static double getVoltage(ISESimulatable Tile, World world) {
        return getEnergyNet(world).simulator.getVoltage(Tile);
    }
    
    public static EnergyNet getEnergyNet(World world){
    	return WorldData.getEnergyNetForWorld(world);
    }
    
    //public static GridDataProvider getGridDataProvider(World world){
    //	return getEnergyNet(world).grid;
    //}
}
