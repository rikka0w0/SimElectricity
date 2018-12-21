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

import net.minecraft.world.WorldServer;
import simelectricity.api.node.ISESimulatable;
import simelectricity.common.ConfigManager;
import simelectricity.common.SELogger;
import simelectricity.energynet.components.SEComponent;
import simelectricity.energynet.components.SwitchA;
import simelectricity.energynet.components.SwitchB;
import simelectricity.energynet.components.VoltageSource;

import java.util.LinkedList;

public final class EnergyNet {
    private final WorldServer world;
    //Contains information about the grid
    protected final EnergyNetDataProvider dataProvider;
    //////////////////////////
    /// Threading
    //////////////////////////
    private EnergyNetSimulator simulator;
    ///////////////////////////////////////////////////////
    ///Event Queue
    ///////////////////////////////////////////////////////
    private final LinkedList<EnergyEventBase> cachedEvents = new LinkedList<>();
    private boolean scheduledRefresh;


    //////////////////////////
    /// Constructor
    //////////////////////////
    public EnergyNet(WorldServer world) {
        this.world = world;
        this.dataProvider = EnergyNetDataProvider.get(world);

        //Initialize thread
        this.simulator = new EnergyNetSimulator(dataProvider, "SEEnergyNet_DIM" + String.valueOf(world.provider.getDimension()));

        SELogger.logInfo(SELogger.general, "EnergyNet has been created for DIM" + world.provider.getDimension());
    }

    public synchronized boolean isNodeValid(ISESimulatable node) {
        return ((SEComponent) node).isValid;
    }

    public synchronized void addEvent(EnergyEventBase event) {
        cachedEvents.add(event);
    }

    /**
     * Called at pre-tick stage
     */
    public synchronized void onPreTick() {
        if (this.simulator.isAlive() && this.simulator.isWorking()) {
            SELogger.logWarn(SELogger.simulator, "Simulation takes longer than usual!");

            while (this.simulator.isWorking()) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (this.simulator.isNewResultAvaliable()) {
            this.dataProvider.onNewResultAvailable();
        }

        boolean needOptimize = false;    //Due to connection changes
        boolean calc = false;            //Perform simulation

        if (this.cachedEvents.isEmpty() && !this.scheduledRefresh)
        	return;
        
        if (this.scheduledRefresh) {
            calc = true;
            needOptimize = true;
            this.scheduledRefresh = false;
        }

        // Copy the cached events to a new list to allow appending more EnergyTiles while processing the event queue
        // Minecraft tend to create a new TileEntity if there is no TE there,
        // this occasionally causes concurrent modification exception. So here is a work around.
        LinkedList<EnergyEventBase> eventsToProcess = new LinkedList<>();
        eventsToProcess.addAll(this.cachedEvents);
        this.cachedEvents.clear();

		/* Events MUST be processed in the following order:
		 * Event Name					|Priority
		 * GridEvent.AppendNode			|1
		 * GridEvent.Connect			|2
		 * GridEvent.MakeTransformer	|2
		 * GridEvent.BreakTranformer	|2
		 * GridEvent.BreakConnection	|2
		 * GridEvent.RemoveNode			|3
		 * TileEvent.Attach				|4
		 * TileEvent.ConnectionChanged	|5
		 * TileEvent.ParamChanged		|5
		 * TileEvent.Detach				|6
		 */
        for (int pass = 0; pass < EnergyEventBase.numOfPass; pass++) {
            //Process EventQueue
            for (EnergyEventBase event : eventsToProcess) {
                event.process(this.dataProvider, pass);
            }
        }

        for (EnergyEventBase event : eventsToProcess) {
            calc |= event.needUpdate();
            needOptimize |= event.changedStructure();
        }

        this.dataProvider.fireGridTileUpdateEvent();

        if (calc) {
            this.simulator.start(needOptimize);
        }
    }

    //////////////////////////
    /// Misc.
    //////////////////////////
    public String[] info() {
        SEGraph tileEntityGraph = this.dataProvider.getTEGraph();
        int iterations = this.simulator.getIterations();
        
        if (tileEntityGraph.size() == 0 && this.dataProvider.getGridObjectCount() == 0) {
            return new String[]{
                    "EnergyNet is empty and idle",
                    "Matrix solving algorithsm: " + ConfigManager.matrixSolver
            };
        }

        if (iterations == 0) {
            return new String[]{
                    "EnergyNet is idle",
                    "Tiles: " + String.valueOf(tileEntityGraph.size()),
                    "Grid Objects: " + String.valueOf(this.dataProvider.getGridObjectCount()),
                    "Matrix solving algorithsm: " + ConfigManager.matrixSolver,
            };
        } else {
            return new String[]{
                    "Time consumption: " + this.simulator.getTimeConsumption() + "ms",
                    "Tiles: " + String.valueOf(tileEntityGraph.size()),
                    "Grid Objects: " + String.valueOf(this.dataProvider.getGridObjectCount()),
                    "Matrix size: " + this.simulator.getMatrixSize(),
                    //"Non-zero elements: " + String.valueOf(this.matrix.getTotalNonZeros()),
                    "Density: " + this.simulator.getDensity() + "%",
                    "Matrix solving algorithsm: " + ConfigManager.matrixSolver,
                    "Iterations:" + String.valueOf(iterations)
            };
        }
    }

    public void reFresh() {
        this.scheduledRefresh = true;
    }

    public void notifyServerShuttingdown() {
        this.simulator.suicide();
    }
    
    public static final double getVoltage(ISESimulatable Tile) {
        SEComponent node = (SEComponent) Tile;
        if (node.eliminated) {
            if (node.optimizedNeighbors.size() == 2) {
                SEComponent A = node.optimizedNeighbors.getFirst();
                SEComponent B = node.optimizedNeighbors.getLast();
                double vA = A.voltageCache;
                double vB = B.voltageCache;
                double rA = node.optimizedResistance.getFirst();
                double rB = node.optimizedResistance.getLast();
                return vA - (vA - vB) * rA / (rA + rB);
            } else if (node.optimizedNeighbors.size() == 1) {
                return node.optimizedNeighbors.getFirst().voltageCache;
            } else if (node.optimizedNeighbors.size() == 0) {
                return 0;
            } else {
                throw new RuntimeException("WTF mate whats going on?!");
            }
        } else {
            return node.voltageCache;
        }
    }

    public static final double getCurrentMagnitude(ISESimulatable Tile) {
        SEComponent node = (SEComponent) Tile;
        if (node.eliminated) {
            if (node.optimizedNeighbors.size() == 2) {
                SEComponent A = node.optimizedNeighbors.getFirst();
                SEComponent B = node.optimizedNeighbors.getLast();
                double vA = A.voltageCache;
                double vB = B.voltageCache;
                double rA = node.optimizedResistance.getFirst();
                double rB = node.optimizedResistance.getLast();
                return Math.abs((vA - vB) / (rA + rB));
            } else if (node.optimizedNeighbors.size() == 1) {
                return 0;
            } else if (node.optimizedNeighbors.size() == 0) {
                return 0;
            } else {
                throw new RuntimeException("WTF mate whats going on?!");
            }
        } else if (node instanceof SwitchA) {
            SwitchA switchA = (SwitchA) node;
            double vA = switchA.voltageCache;
            double vB = switchA.getComplement().voltageCache;
            return Math.abs((vA - vB) / switchA.getResistance());
        } else if (node instanceof SwitchB) {
            SwitchB switchB = (SwitchB) node;
            double vA = switchB.voltageCache;
            double vB = switchB.getComplement().voltageCache;
            return Math.abs((vA - vB) / switchB.getResistance());
        } else if (node instanceof VoltageSource) {
            VoltageSource vs = (VoltageSource) node;
            return Math.abs((vs.voltageCache - vs.getOutputVoltage()) / vs.getResistance());
        }

        return Double.NaN;
    }
}
