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
}
