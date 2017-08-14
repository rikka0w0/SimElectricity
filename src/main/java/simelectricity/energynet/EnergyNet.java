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

package simelectricity.energynet;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import simelectricity.api.IEnergyNetUpdateHandler;
import simelectricity.api.node.ISESimulatable;
import simelectricity.common.ConfigManager;
import simelectricity.common.SEUtils;
import simelectricity.energynet.components.SEComponent;
import simelectricity.energynet.matrix.IMatrixResolver;

import java.util.*;

public final class EnergyNet extends EnergyNetSimulator implements Runnable{
	private final WorldServer world;
	///////////////////////////////////////////////////////
	///Event Queue
	///////////////////////////////////////////////////////
	private LinkedList<EnergyEventBase> cachedEvents = new LinkedList<EnergyEventBase>();
	private boolean scheduledRefresh = false;
	
	public synchronized void addEvent(EnergyEventBase event){
		this.cachedEvents.add(event);
	}
	
	/**
	 * Called at pre-tick stage
	 */
	public synchronized void onPreTick(){
		if (this.processing){
			SEUtils.logWarn("Simulation takes longer than usual!", SEUtils.simulator);
			while(this.processing)
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
		
		if (cachedEvents.isEmpty())
			return;
		
		boolean needOptimize = false;	//Due to connection changes
		boolean calc = false;			//Perform simulation
		
		if (scheduledRefresh){
			calc = true;
			needOptimize = true;
			scheduledRefresh = false;
		}
		
		/* Events MUST be processed in the following order:
		 * Event Name					|Priority
		 * GridEvent.AppendNode			|1
		 * GridEvent.Connect			|2
		 * GridEvent.MakeTransformer	|2
		 * GridEvent.BreakTranformer	|2
		 * GridEvent.BreakConnection	|2
		 * GridEvent.RemoveNode			|1
		 * TileEvent.Attach				|3
		 * TileEvent.ConnectionChanged	|4
		 * TileEvent.ParamChanged		|4
		 * TileEvent.Detach				|3
		 */
		for (int priority = 1; priority <= EnergyEventBase.numOfPriority; priority++) {
			Iterator<EnergyEventBase> iterator = cachedEvents.iterator();
			
			//Process EventQueue
			while (iterator.hasNext()){
				EnergyEventBase event = iterator.next();
				
				//Process event
				if (event.priority == priority) {
					event.process(dataProvider);
					calc |= event.needUpdate;
					needOptimize |= event.changedStructure;
					iterator.remove(); //Remove from the queue
				}
			}
		}
		
		cachedEvents.clear();
		
		if (calc){
			this.needOptimize = needOptimize;
			thread.interrupt();
		}
	}
	
	public synchronized static boolean isNodeValid(ISESimulatable node){
		return ((SEComponent)node).isValid;
	}
	
    //////////////////////////
    /// Constructor
    //////////////////////////
    public EnergyNet(WorldServer world) { 
    	super(	Math.pow(10, -ConfigManager.precision),
    			1.0D/ConfigManager.shuntPN,
    			IMatrixResolver.MatrixHelper.newSolver(ConfigManager.matrixSolver),
    			EnergyNetDataProvider.get(world));
    	this.world = world;
    	
    	//Initialize thread
    	this.thread = new Thread(this, "SEEnergyNet_DIM" + String.valueOf(world.provider.getDimension()));
    	this.alive = true;
    	this.processing = false;
	    this.thread.start();
    	
        SEUtils.logInfo("EnergyNet has been created for DIM" + String.valueOf(world.provider.getDimension()), SEUtils.general);
    }
    
    
    //////////////////////////
    /// Misc.
    //////////////////////////
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
        			"Time consumption: " + String.valueOf(this.duration) + "ms",
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

	

    
    //////////////////////////
    /// Threading
    //////////////////////////
    private final Thread thread;
    
    private volatile boolean needOptimize;	//Set to true to launch the optimizer
	private volatile boolean alive;			//Set to false to kill the energyNet thread
	private volatile boolean processing;	//An indicator of the energyNet state
	private volatile long duration;			//Time taken for the latest simulation, in milliseconds

	public boolean hasValidState(){
		return !processing;
	}
	
	/**
	 * @return thread name, e.g. SEEnergyNet_DIM0
	 */
    public String getThreadName(){
    	return thread.getName();
    }
	
	public void notifyServerShuttingdown(){
		this.alive = false;
	}
	
    @Override
	public void run() {
		long startAt;
		while(alive){
			try {			
				SEUtils.logInfo(getThreadName() + " Sleep", SEUtils.simulator);
				while (alive)
					thread.sleep(1);
			} catch (InterruptedException e) {
				SEUtils.logInfo(getThreadName() + " wake up", SEUtils.simulator);
				
				if (!alive)
					break;
				
				this.processing = true;
				SEUtils.logInfo(getThreadName() + " Started", SEUtils.simulator);
				startAt = System.currentTimeMillis();
				runSimulator(needOptimize);
				SEUtils.logInfo(getThreadName() + " Done", SEUtils.simulator);
				duration = (System.currentTimeMillis() - startAt);

				//Execute Handlers
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
				
				this.processing = false;
			}
		}
		SEUtils.logInfo(getThreadName() + " is shutting down", SEUtils.general);
	}
}
