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
import simelectricity.energynet.matrix.IMatrixResolver.MatrixHelper;

import java.util.Iterator;
import java.util.LinkedList;

public final class EnergyNet extends EnergyNetSimulator implements Runnable {
    private final WorldServer world;
    //////////////////////////
    /// Threading
    //////////////////////////
    private final Thread thread;
    ///////////////////////////////////////////////////////
    ///Event Queue
    ///////////////////////////////////////////////////////
    private final LinkedList<EnergyEventBase> cachedEvents = new LinkedList<EnergyEventBase>();
    private boolean scheduledRefresh;
    private volatile boolean needOptimize;    //Set to true to launch the optimizer
    private volatile boolean alive;            //Set to false to kill the energyNet thread
    private volatile boolean processing;    //An indicator of the energyNet state
    private volatile long duration;            //Time taken for the latest simulation, in milliseconds

    //////////////////////////
    /// Constructor
    //////////////////////////
    public EnergyNet(WorldServer world) {
        super(Math.pow(10, -ConfigManager.precision),
                1.0D / ConfigManager.shuntPN,
                MatrixHelper.newSolver(ConfigManager.matrixSolver),
                EnergyNetDataProvider.get(world));
        this.world = world;

        //Initialize thread
        thread = new Thread(this, "SEEnergyNet_DIM" + String.valueOf(world.provider.getDimension()));
        alive = true;
        processing = false;
        thread.start();

        SEUtils.logInfo("EnergyNet has been created for DIM" + String.valueOf(world.provider.getDimension()), SEUtils.general);
    }

    public static synchronized boolean isNodeValid(ISESimulatable node) {
        return ((SEComponent) node).isValid;
    }

    public synchronized void addEvent(EnergyEventBase event) {
        cachedEvents.add(event);
    }

    /**
     * Called at pre-tick stage
     */
    public synchronized void onPreTick() {
        if (processing) {
            SEUtils.logWarn("Simulation takes longer than usual!", SEUtils.simulator);
            while (processing)
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }


        boolean needOptimize = false;    //Due to connection changes
        boolean calc = false;            //Perform simulation

        if (this.scheduledRefresh) {
            calc = true;
            needOptimize = true;
            this.scheduledRefresh = false;
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
            Iterator<EnergyEventBase> iterator = this.cachedEvents.iterator();

            //Process EventQueue
            while (iterator.hasNext()) {
                EnergyEventBase event = iterator.next();

                //Process event
                if (event.priority == priority) {
                    event.process(this.dataProvider);
                    calc |= event.needUpdate;
                    needOptimize |= event.changedStructure;
                    iterator.remove(); //Remove from the queue
                }
            }
        }

        this.cachedEvents.clear();
        this.dataProvider.fireGridTileUpdateEvent();

        if (calc) {
            this.needOptimize = needOptimize;
            this.thread.interrupt();
        }
    }

    //////////////////////////
    /// Misc.
    //////////////////////////
    public String[] info() {
        SEGraph tileEntityGraph = this.dataProvider.getTEGraph();
        String density;

        if (tileEntityGraph.size() == 0 && this.dataProvider.getGridObjectCount() == 0) {
            return new String[]{
                    "EnergyNet is empty and idle",
                    "Matrix solving algorithsm: " + ConfigManager.matrixSolver
            };
        }

        if (this.matrix.getMatrixSize() == 0) {
            density = "Undefined";
        } else {
            density = this.matrix.getTotalNonZeros() * 100 / this.matrix.getMatrixSize() / this.matrix.getMatrixSize() + "%";
        }

        if (this.iterations == 0) {
            return new String[]{
                    "EnergyNet is idle",
                    "Tiles: " + String.valueOf(tileEntityGraph.size()),
                    "Grid Objects: " + String.valueOf(this.dataProvider.getGridObjectCount()),
                    "Matrix solving algorithsm: " + ConfigManager.matrixSolver,
            };
        } else {
            return new String[]{
                    "Time consumption: " + String.valueOf(duration) + "ms",
                    "Tiles: " + String.valueOf(tileEntityGraph.size()),
                    "Grid Objects: " + String.valueOf(this.dataProvider.getGridObjectCount()),
                    "Matrix size: " + String.valueOf(this.matrix.getMatrixSize()),
                    "Non-zero elements: " + String.valueOf(this.matrix.getTotalNonZeros()),
                    "Density: " + density,
                    "Matrix solving algorithsm: " + ConfigManager.matrixSolver,
                    "Iterations:" + String.valueOf(this.iterations)
            };
        }
    }

    public void reFresh() {
        this.scheduledRefresh = true;
    }

    public boolean hasValidState() {
        return !this.processing;
    }

    /**
     * @return thread name, e.g. SEEnergyNet_DIM0
     */
    public String getThreadName() {
        return this.thread.getName();
    }

    public void notifyServerShuttingdown() {
        alive = false;
    }

    @Override
    public void run() {
        long startAt;
        while (this.alive) {
            try {
                SEUtils.logInfo(this.getThreadName() + " Sleep", SEUtils.simulator);
                while (this.alive)
                    Thread.sleep(1);
            } catch (InterruptedException e) {
                SEUtils.logInfo(this.getThreadName() + " wake up", SEUtils.simulator);

                if (!this.alive)
                    break;

                processing = true;
                SEUtils.logInfo(this.getThreadName() + " Started", SEUtils.simulator);
                startAt = System.currentTimeMillis();
                this.runSimulator(this.needOptimize);
                SEUtils.logInfo(this.getThreadName() + " Done", SEUtils.simulator);
                this.duration = System.currentTimeMillis() - startAt;

                //Execute Handlers
                Iterator<TileEntity> iterator = this.dataProvider.getLoadedTileIterator();
                while (iterator.hasNext()) {
                    TileEntity te = iterator.next();
                    if (te instanceof IEnergyNetUpdateHandler)
                        ((IEnergyNetUpdateHandler) te).onEnergyNetUpdate();
                }
                iterator = this.dataProvider.getLoadedGridTileIterator();
                while (iterator.hasNext()) {
                    TileEntity te = iterator.next();
                    if (te instanceof IEnergyNetUpdateHandler)
                        ((IEnergyNetUpdateHandler) te).onEnergyNetUpdate();
                }

                processing = false;
            }
        }
        SEUtils.logInfo(this.getThreadName() + " is shutting down", SEUtils.general);
    }
}
