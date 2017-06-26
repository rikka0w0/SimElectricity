package simelectricity.energynet.components;

import java.util.LinkedList;

import net.minecraft.tileentity.TileEntity;

import simelectricity.api.components.ISEComponentParameter;
import simelectricity.api.node.ISESimulatable;
import simelectricity.api.node.ISESubComponent;

public abstract class SEComponent implements ISESimulatable, ISEComponentParameter{
	public boolean isValid = false;
	/**
	 * Host TileEntity for Tiles and Associated TileEntity for GridTiles
	 */
	public TileEntity te;
	
	
	//Optimization and simulation runtime
	public boolean visited;
	public boolean eliminated = false;
	public LinkedList<SEComponent> optimizedNeighbors = new LinkedList<SEComponent>();
	public LinkedList<Double> optimizedResistance = new LinkedList<Double>();
	public int index;
	
	
	
	public volatile double voltageCache = 0;
	
	
	/**
	 * @param <TYPE> extends ISEComponentParameter
	 */
	public static abstract class Tile<TYPE extends ISEComponentParameter> extends SEComponent{
		protected final TYPE dataProvider;
		
		public Tile(TYPE dataProvider, TileEntity te){
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
	
	/////////////////////////////
	/// ISESimulatable
	/////////////////////////////
	/**Only two port networks need to override this!*/
	@Override
	public ISESubComponent getComplement() {return null;}
	
	/**Only regulator need to override this!*/
	@Override
	public ISESubComponent getComplement2() {return null;}
	
	@Override
	public ISEComponentParameter getCachedParameters() {
		return this;
	}
	
	/**
	 * Adjacency lists, part of graph
	 */
	public LinkedList<SEComponent> neighbors = new LinkedList<SEComponent>();
}
