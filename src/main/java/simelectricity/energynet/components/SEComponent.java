package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISEComponentParameter;
import simelectricity.api.node.ISESimulatable;

import java.util.LinkedList;

public abstract class SEComponent implements ISESimulatable, ISEComponentParameter {
    public boolean isValid;
    /**
     * Host TileEntity for Tiles and Associated TileEntity for GridTiles
     */
    public TileEntity te;


    //Optimization and simulation runtime
    public boolean visited;
    public boolean eliminated;
    public LinkedList<SEComponent> optimizedNeighbors = new LinkedList<SEComponent>();
    public LinkedList<Double> optimizedResistance = new LinkedList<Double>();
    public int index;

    public volatile double newVoltage;
    public volatile double voltageCache;
    /**
     * Adjacency lists, part of graph
     */
    public LinkedList<SEComponent> neighbors = new LinkedList<SEComponent>();


    /**
     * @param <TYPE> extends ISEComponentParameter
     */
    public abstract static class Tile<TYPE extends ISEComponentParameter> extends SEComponent {
        protected final TYPE dataProvider;

        public Tile(TYPE dataProvider, TileEntity te) {
            this.dataProvider = dataProvider;
            this.te = te;
        }

        /**
         * Parent class stores parameters of the component (Internal state), the only way to update them is calling this function
         * </p>
         * DO NOT alter the internal state anywhere else, otherwise it can cause unpredictable results
         */
        public abstract void updateComponentParameters();
    }

    @Override
    public double getVoltage() {
        if (this.eliminated) {
            if (this.optimizedNeighbors.size() == 2) {
                SEComponent A = this.optimizedNeighbors.getFirst();
                SEComponent B = this.optimizedNeighbors.getLast();
                double vA = A.voltageCache;
                double vB = B.voltageCache;
                double rA = this.optimizedResistance.getFirst();
                double rB = this.optimizedResistance.getLast();
                return vA - (vA - vB) * rA / (rA + rB);
            } else if (this.optimizedNeighbors.size() == 1) {
                return this.optimizedNeighbors.getFirst().voltageCache;
            } else if (this.optimizedNeighbors.size() == 0) {
                return 0;
            } else {
                throw new RuntimeException("WTF mate whats going on?!");
            }
        } else {
            return this.voltageCache;
        }
    }

    @Override
    public double getCurrentMagnitude() {
        if (this.eliminated) {
            if (this.optimizedNeighbors.size() == 2) {
                SEComponent A = this.optimizedNeighbors.getFirst();
                SEComponent B = this.optimizedNeighbors.getLast();
                double vA = A.voltageCache;
                double vB = B.voltageCache;
                double rA = this.optimizedResistance.getFirst();
                double rB = this.optimizedResistance.getLast();
                return Math.abs((vA - vB) / (rA + rB));
            } else if (this.optimizedNeighbors.size() == 1) {
                return 0;
            } else if (this.optimizedNeighbors.size() == 0) {
                return 0;
            } else {
                throw new RuntimeException("WTF mate whats going on?!");
            }
        } else if (this instanceof SwitchA) {
            SwitchA switchA = (SwitchA) this;
            double vA = switchA.voltageCache;
            double vB = switchA.getComplement().voltageCache;
            return Math.abs((vA - vB) / switchA.getResistance());
        } else if (this instanceof SwitchB) {
            SwitchB switchB = (SwitchB) this;
            double vA = switchB.voltageCache;
            double vB = switchB.getComplement().voltageCache;
            return Math.abs((vA - vB) / switchB.getResistance());
        } else if (this instanceof VoltageSource) {
            VoltageSource vs = (VoltageSource) this;
            return Math.abs((vs.voltageCache - vs.getOutputVoltage()) / vs.getResistance());
        }

        return Double.NaN;
    }



    @Override
    public boolean hasResistiveConnection(ISESimulatable neighbor) {
        return neighbors.contains(neighbor);
    }
}
