package simElectricity.EnergyNet.Components;

import java.util.LinkedList;

import net.minecraft.tileentity.TileEntity;

import simElectricity.API.DataProvider.ISEComponentDataProvider;
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.EnergyTile.ISESubComponent;

public abstract class SEComponent implements ISESimulatable{
	/**
	 * Host TileEntity for Tiles and Associated TileEntity for GridTiles
	 */
	public TileEntity te;
	
	//Only two port networks need to override this!
	@Override
	public ISESubComponent getComplement() {return null;}
	
	//Optimization and simulation runtime
	public boolean visited;
	public boolean eliminated = false;
	public LinkedList<SEComponent> optimizedNeighbors = new LinkedList<SEComponent>();
	public LinkedList<Double> optimizedResistance = new LinkedList<Double>();
	public int index;
	
	public volatile double voltageCache = 0;
	
	
	/**
	 * @param <TYPE> extends ISEComponentDataProvider
	 */
	public static abstract class Tile<TYPE extends ISEComponentDataProvider> extends SEComponent{
		protected TYPE dataProvider;
		
		public Tile(TYPE dataProvider, TileEntity te){
			this.dataProvider = dataProvider;
			this.te = te;
			//updateComponentParameters();
		}
		
		/**
		 * Parent class stores parameters of the component (Internal state), the only way to update them is calling this function
		 * </p>
		 * DO NOT alter the internal state anywhere else, otherwise it can cause unpredictable results
		 */
		public abstract void updateComponentParameters();
	}
	
	
	/**
	 * Adjacency lists, part of graph
	 */
	public LinkedList<SEComponent> neighbors = new LinkedList<SEComponent>();
}
