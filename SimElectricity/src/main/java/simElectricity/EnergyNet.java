package simElectricity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import simElectricity.API.IBaseComponent;
import simElectricity.API.IEnergyTile;
import simElectricity.API.TileAttachEvent;
import simElectricity.API.TileChangeEvent;
import simElectricity.API.TileDetachEvent;

public final class EnergyNet {
	/*Simulator*/
	private static final float EPSILON = (float) 1e-10;
	// private WeightedMultigraph<IBaseComponent, Resistor> tileEntityGraph =
	// new WeightedMultigraph<IBaseComponent, Resistor>(Resistor.class);
	private SimpleGraph<IBaseComponent, DefaultEdge> tileEntityGraph = new SimpleGraph<IBaseComponent, DefaultEdge>(DefaultEdge.class);
	public Map<IBaseComponent, Float> voltageCache = new HashMap<IBaseComponent, Float>();
	/** A flag for recalculate the energynet*/
	private boolean calc = false;	
	
	// Gaussian elimination with partial pivoting
	private static float[] lsolve(float[][] A, float[] b) {
		int N = b.length;

		for (int p = 0; p < N; p++) {

			// find pivot row and swap
			int max = p;
			for (int i = p + 1; i < N; i++) {
				if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
					max = i;
				}
			}
			float[] temp = A[p];
			A[p] = A[max];
			A[max] = temp;
			float t = b[p];
			b[p] = b[max];
			b[max] = t;

			// singular or nearly singular
			if (Math.abs(A[p][p]) <= EPSILON) {
				//throw new RuntimeException(	"Matrix is singular or nearly singular");
			}

			// pivot within A and b
			for (int i = p + 1; i < N; i++) {
				float alpha = A[i][p] / A[p][p];
				b[i] -= alpha * b[p];
				for (int j = p; j < N; j++) {
					A[i][j] -= alpha * A[p][j];
				}
			}
		}

		// back substitution
		float[] x = new float[N];
		for (int i = N - 1; i >= 0; i--) {
			float sum = (float) 0.0;
			for (int j = i + 1; j < N; j++) {
				sum += A[i][j] * x[j];
			}
			x[i] = (b[i] - sum) / A[i][i];
		}
		return x;
	}

	/** Internal use only, return a list containing neighbor TileEntities*/
	private static List<TileEntity> neighborListOf(TileEntity te) {
		List<TileEntity> result = new ArrayList<TileEntity>();
		TileEntity temp;

		temp = te.getWorldObj().getTileEntity(te.xCoord + 1, te.yCoord,
				te.zCoord);
		if (temp instanceof IBaseComponent)
			result.add(temp);
		temp = te.getWorldObj().getTileEntity(te.xCoord - 1, te.yCoord,
				te.zCoord);
		if (temp instanceof IBaseComponent)
			result.add(temp);
		temp = te.getWorldObj().getTileEntity(te.xCoord, te.yCoord + 1,
				te.zCoord);
		if (temp instanceof IBaseComponent)
			result.add(temp);
		temp = te.getWorldObj().getTileEntity(te.xCoord, te.yCoord - 1,
				te.zCoord);
		if (temp instanceof IBaseComponent)
			result.add(temp);
		temp = te.getWorldObj().getTileEntity(te.xCoord, te.yCoord,
				te.zCoord + 1);
		if (temp instanceof IBaseComponent)
			result.add(temp);
		temp = te.getWorldObj().getTileEntity(te.xCoord, te.yCoord,
				te.zCoord - 1);
		if (temp instanceof IBaseComponent)
			result.add(temp);

		return result;
	}

	private void mergeNodes(List<IBaseComponent> nodes) {
		// for (int i = nodes.size() - 1; i >= 0; i--) {
		// IBaseComponent thisNode = nodes.get(i);
		// if (tileEntityGraph.degreeOf(thisNode) == 2) {
		// List<IBaseComponent> neighborList = Graphs.neighborListOf(
		// tileEntityGraph, thisNode);
		//
		// }
		// }
	}

	private void runSimulator() {
		List<IBaseComponent> unknownVoltageNodes = new ArrayList<IBaseComponent>();
		unknownVoltageNodes.addAll(tileEntityGraph.vertexSet());

		int matrixSize = unknownVoltageNodes.size();
		//if(matrixSize < 3)
		//	return;

		float[][] A = new float[matrixSize][matrixSize];
		float[] b = new float[matrixSize];

		for (int i = 0; i < matrixSize; i++) {		
			IBaseComponent nodeI = unknownVoltageNodes.get(i);
			
			if (nodeI instanceof IEnergyTile) {
				b[i] = 1 / nodeI.getResistance();
				b[i] = b[i]	* ((IEnergyTile) nodeI).getOutputVoltage();
			}

			List<IBaseComponent> neighborList = Graphs.neighborListOf(tileEntityGraph, nodeI);	
			for (int j = 0; j < matrixSize; j++) {
				float tmp = 0;
				if (i == j) {
					for (IBaseComponent iBaseComponent : neighborList)
						// add neighbor resistance
						tmp += 1.0 / (getResistance(nodeI) + getResistance(iBaseComponent));
					if (nodeI instanceof IEnergyTile) 
						tmp += 1.0 / nodeI.getResistance();
				} else {
					if (neighborList.contains(unknownVoltageNodes.get(j)))
						// add neighbor resistance
						tmp = (float) (-1.0 / (getResistance(nodeI) + getResistance(unknownVoltageNodes.get(j))));
				}
				A[i][j] = tmp;
			}
		}
		
		float[] x = lsolve(A, b);
		
		voltageCache.clear();
		for (int i = 0; i < x.length; i++) {
			voltageCache.put(unknownVoltageNodes.get(i), x[i]);
		}
	}

	/** Internal use only, only used by runSimulator()*/
	private static float getResistance(IBaseComponent te){
		if (te instanceof IEnergyTile)
			return 0;
		else
			return te.getResistance();
	}
	/*End of Simulator*/
	
	
	
	/** Called in each tick to attempt to do calculation*/
	public static void onTick(World world) {
		EnergyNet energyNet = getForWorld(world);
		if(energyNet.calc == true){
			energyNet.calc = false;
			energyNet.runSimulator();
		}
	}
	
	/** Add a TileEntity to the energynet*/
	public void addTileEntity(TileEntity te) {
		if(!te.getWorldObj().blockExists(te.xCoord, te.yCoord, te.zCoord)){
			System.out.println(te
					+ " is added to the energy net too early!, aborting");
			return;			
		}
		
		if(te.isInvalid()){
			System.out.println("Invalid tileentity " + te
					+ " is trying to attach to energy network, aborting");
			return;			
		}
		
		if (!(te instanceof IBaseComponent)) {
			System.out.println("Unacceptable tileentity " + te
					+ " is trying to attach to energy network, aborting");
			return;
		}
		
		List<TileEntity> neighborList = neighborListOf(te);
		
		if(!tileEntityGraph.containsVertex((IBaseComponent) te))
			tileEntityGraph.addVertex((IBaseComponent) te);
		for (TileEntity tileEntity : neighborList) {
			if(!tileEntityGraph.containsVertex((IBaseComponent) tileEntity))
				tileEntityGraph.addVertex((IBaseComponent) tileEntity);
			tileEntityGraph.addEdge((IBaseComponent) te,
					(IBaseComponent) tileEntity);
		}

		calc = true;
		
		System.out.println("Tileentity " + te
				+ " is attached to energy network!");
	}

	/** Remove a TileEntiy from the energy net*/
	public void removeTileEntity(TileEntity te) {
		tileEntityGraph.removeVertex((IBaseComponent) te);
		
		calc = true;
		
		System.out.println("Tileentity " + te + " is detach to energy network!");
	}

	/** Mark the energy net for updating in next tick*/
	public void markForUpdate(TileEntity te){
		calc = true;
		
		System.out.println("Tileentity " + te + " cause the energy network to update!");
	}
	
	/** Return a instance of energynet for a specific world*/
	public static EnergyNet getForWorld(World world) {
		WorldData worldData = WorldData.get(world);
		return worldData.energyNet;
	}

	/** Initialize the energy network, basically register some forge events */
	public static void initialize() {
		new EventHandler();
	}

	/** Creation of the energy network*/
	public EnergyNet() {
		System.out.println("EnergyNet create");
	}
	
	/** Response to forge events */
	public static class EventHandler {
		public EventHandler() {
			MinecraftForge.EVENT_BUS.register(this);
		}

		@SubscribeEvent
		public void onAttachEvent(TileAttachEvent event) {
			EnergyNet.getForWorld(event.energyTile.getWorldObj()).addTileEntity(
					(TileEntity) event.energyTile);
		}

		@SubscribeEvent
		public void onTileDetach(TileDetachEvent event) {
			EnergyNet.getForWorld(event.energyTile.getWorldObj()).removeTileEntity(
					(TileEntity) event.energyTile);
		}

		@SubscribeEvent
		public void onTileChange(TileChangeEvent event) {
			EnergyNet.getForWorld(event.energyTile.getWorldObj()).markForUpdate(
					(TileEntity) event.energyTile);
		}
	}
}
