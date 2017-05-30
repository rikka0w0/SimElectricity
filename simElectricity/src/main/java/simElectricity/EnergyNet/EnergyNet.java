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

package simElectricity.EnergyNet;

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
import simElectricity.EnergyNet.TileEvent.Detach;
import simElectricity.EnergyNet.Components.ConstantPowerLoad;
import simElectricity.EnergyNet.Components.DiodeInput;
import simElectricity.EnergyNet.Components.DiodeOutput;
import simElectricity.EnergyNet.Components.GridNode;
import simElectricity.EnergyNet.Components.RegulatorController;
import simElectricity.EnergyNet.Components.RegulatorInput;
import simElectricity.EnergyNet.Components.RegulatorOutput;
import simElectricity.EnergyNet.Components.SEComponent;
import simElectricity.EnergyNet.Components.TransformerPrimary;
import simElectricity.EnergyNet.Components.TransformerSecondary;
import simElectricity.EnergyNet.Components.VoltageSource;
import simElectricity.EnergyNet.Matrix.IMatrixResolver;
import simElectricity.API.SEAPI;
import sun.security.ssl.Debug;

import java.util.*;

public final class EnergyNet extends EnergyNetSimulator{
	private EnergyNetThread thread;
	
	///////////////////////////////////////////////////////
	///Event Queue
	///////////////////////////////////////////////////////
	private LinkedList<IEnergyNetEvent> cachedEvents = new LinkedList<IEnergyNetEvent>();
	private LinkedList<IEnergyNetEvent> events = new LinkedList<IEnergyNetEvent>();
	
	public void addEvent(IEnergyNetEvent event){
		synchronized (this){
			this.cachedEvents.add(event);
		}
	}
	
	/**
	 * Called at pre-tick stage
	 */
	public void onPreTick(){
		boolean needOptimize = false;	//Due to connection changes
		boolean calc = false;			//Perform simulation
		
		synchronized (this){
			this.events.clear();
			this.events.addAll(cachedEvents);
			this.cachedEvents.clear();
		}

		Iterator<IEnergyNetEvent> iterator = events.iterator();
		
		if (!iterator.hasNext())
			return;				//Nothing changed
		
		//Process EventQueue
		while (iterator.hasNext()){
			IEnergyNetEvent event = iterator.next();
			
			
			//Process event, update graph
			if (event instanceof TileEvent){
				TileEvent tileEvent = (TileEvent) event; 
				
				if (event instanceof TileEvent.Attach){
					needOptimize = true;
					calc = true;
					dataProvider.registerTile(tileEvent.te);
					dataProvider.updateTileParam(tileEvent.te);
				}else if (event instanceof TileEvent.Detach){
					needOptimize = true;
					calc = true;
					dataProvider.unregisterTile(tileEvent.te);
					dataProvider.updateTileParam(tileEvent.te);
				}else if (event instanceof TileEvent.ConnectionChanged){
					needOptimize = true;
					calc = true;
					dataProvider.unregisterTile(tileEvent.te);
					dataProvider.registerTile(tileEvent.te);
					dataProvider.updateTileParam(tileEvent.te);
				}else if (event instanceof TileEvent.ParamChanged){
					calc = true;
					dataProvider.updateTileParam(tileEvent.te);
				}else if (event instanceof TileEvent.GridTilePresent){
					dataProvider.onGridTilePresent(tileEvent.te);
				}else if (event instanceof TileEvent.GridTileInvalidate){
					dataProvider.onGridTileInvalidate(tileEvent.te);
				}
			}

			if (event instanceof GridEvent){
				GridEvent gridEvent = (GridEvent) event;
				if (event instanceof GridEvent.AppendNode){
					needOptimize = true;
					calc = true;
					dataProvider.addGridNode(gridEvent.x1, gridEvent.y1, gridEvent.z1, ((GridEvent.AppendNode)event).type);
				}else if (event instanceof GridEvent.RemoveNode){
			    	GridNode node = dataProvider.getGridObjectAtCoord(gridEvent.x1, gridEvent.y1, gridEvent.z1);
			    	if (node != null){
			    		needOptimize = true;
						calc = true;
			    		dataProvider.removeGridNode(node);
			    	}
				}else if (event instanceof GridEvent.Connect){
					GridEvent.Connect connectEvent = ((GridEvent.Connect)event);
					
			    	GridNode node1 = dataProvider.getGridObjectAtCoord(connectEvent.x1,connectEvent.y1,connectEvent.z1);
			    	GridNode node2 = dataProvider.getGridObjectAtCoord(connectEvent.x2,connectEvent.y2,connectEvent.z2);
			    	
			    	if (node1 != null && node2 != null){
			    		needOptimize = true;
						calc = true;
			    		dataProvider.addGridConnection(node1, node2, connectEvent.resistance);
			    	}
				}else if (event instanceof GridEvent.BreakConnection){
					GridEvent.BreakConnection breakConEvent = ((GridEvent.BreakConnection)event);
					
			    	GridNode node1 = dataProvider.getGridObjectAtCoord(breakConEvent.x1,breakConEvent.y1,breakConEvent.z1);
			    	GridNode node2 = dataProvider.getGridObjectAtCoord(breakConEvent.x2,breakConEvent.y2,breakConEvent.z2);
			    	
			    	if (node1 != null && node2 != null){
			    		needOptimize = true;
						calc = true;
			    		dataProvider.removeGridConnection(node1, node2);
			    	}
				}
			}
			
		}
		
		if (calc)
			thread.wakeUp(needOptimize);
	}
	
	public void executeHandlers(){
		Iterator<TileEntity> iterator = dataProvider.getLoadedTileIterator();
		while(iterator.hasNext()){
			TileEntity te = iterator.next();
			if (te instanceof IEnergyNetUpdateHandler)
				((IEnergyNetUpdateHandler)te).onEnergyNetUpdate();
		}
		iterator = dataProvider.getLoadedGridTileIterator();
		while(iterator.hasNext()){
			TileEntity te = iterator.next();
			if (te instanceof IEnergyNetUpdateHandler)
				((IEnergyNetUpdateHandler)te).onEnergyNetUpdate();
		}
	}
    

    public EnergyNet(World world) { 
    	//Initialize simulator
    	epsilon = Math.pow(10, -ConfigManager.precision);
    	Gpn = 1.0D/ConfigManager.shuntPN;
    	matrix = IMatrixResolver.MatrixHelper.newResolver(ConfigManager.matrixSolver);
    	
    	//Initialize data provider
    	dataProvider = EnergyNetDataProvider.get(world);
    	
    	//Initialize thread
    	thread = EnergyNetThread.create(world.provider.dimensionId, this);
    	
        SEUtils.logInfo("EnergyNet has been created for DIM" + String.valueOf(world.provider.dimensionId));
    }
    
	public void shutdown(){
		thread.terminate();
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

    	if (matrix.getMatrixSize() == 0){
    		density = "Undefined";
    	}else{
    		density = String.valueOf(matrix.getTotalNonZeros() * 100 / matrix.getMatrixSize()/ matrix.getMatrixSize()) + "%";
    	}
    	
    	if (iterations == 0){
        	return new String[]{
        			"EnergyNet is idle",
        	    	"Loaded entities: " + String.valueOf(tileEntityGraph.size()),
        	    	"Grid Objects: " + String.valueOf(dataProvider.getGridObjectCount()),
        	    	"Matrix solving algorithsm: " + ConfigManager.matrixSolver,
        	    	};   		
    	}else{
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
    }

    
    public void reFresh(){
    	//onPreTick();
    	thread.wakeUp(true);
    }

    public double getVoltage(ISESimulatable Tile){   	
    	SEComponent node = (SEComponent) Tile;
    	if (node.eliminated){
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
    	}else{
    		return node.voltageCache;
    	}
   }
    
    
    public double getCurrentMagnitude(ISESimulatable Tile){
    	SEGraph graph = dataProvider.getTEGraph();
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
    	/*
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

    	}*/
    }
}
