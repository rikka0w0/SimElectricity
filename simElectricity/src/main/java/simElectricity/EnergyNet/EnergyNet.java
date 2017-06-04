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
import simElectricity.API.IEnergyNetUpdateHandler;
import simElectricity.Common.ConfigManager;
import simElectricity.Common.SEUtils;
import simElectricity.EnergyNet.Components.GridNode;
import simElectricity.EnergyNet.Matrix.IMatrixResolver;

import java.util.*;

public final class EnergyNet extends EnergyNetSimulator{
	private EnergyNetThread thread;
	private boolean scheduledRefresh = false;
	
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
		if (thread.isWorking()){
			SEUtils.logWarn("Simulation takes longer than usual!", SEUtils.simulator);
			while(thread.isWorking())
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
		
		boolean needOptimize = false;	//Due to connection changes
		boolean calc = false;			//Perform simulation
		
		if (scheduledRefresh){
			calc = true;
			calc = false;
		}
			
		
		synchronized (this){
			this.events.clear();
			this.events.addAll(cachedEvents);
			this.cachedEvents.clear();
		}

		Iterator<IEnergyNetEvent> iterator = events.iterator();
		
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
    	
        SEUtils.logInfo("EnergyNet has been created for DIM" + String.valueOf(world.provider.dimensionId), SEUtils.general);
    }
    
	public void notifyServerShuttingdown(){
		thread.alive = false;
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
        	    	"Tiles: " + String.valueOf(tileEntityGraph.size()),
        	    	"Grid Objects: " + String.valueOf(dataProvider.getGridObjectCount()),
        	    	"Matrix solving algorithsm: " + ConfigManager.matrixSolver,
        	    	};   		
    	}else{
        	return new String[]{
        			"Time consumption:" + String.valueOf(thread.lastDuration()),
        	    	"Tiles: " + String.valueOf(tileEntityGraph.size()),
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
    	scheduledRefresh = true;
    }
}
