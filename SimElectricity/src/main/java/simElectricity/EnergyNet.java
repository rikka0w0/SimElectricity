package simElectricity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import simElectricity.API.*;

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

		if (te instanceof IConductor){		
			temp = te.getWorldObj().getTileEntity(te.xCoord + 1, te.yCoord,te.zCoord);	
			if (temp instanceof IConductor){
				result.add(temp);
			}else if (temp instanceof IEnergyTile){
				if(((IEnergyTile)temp).getFunctionalSide().getOpposite()==ForgeDirection.EAST)
					result.add(temp);
			}
			
			temp = te.getWorldObj().getTileEntity(te.xCoord - 1, te.yCoord,te.zCoord);
			if (temp instanceof IConductor){
				result.add(temp);
			}else if (temp instanceof IEnergyTile){
				if(((IEnergyTile)temp).getFunctionalSide().getOpposite()==ForgeDirection.WEST)
					result.add(temp);
			}
			
			temp = te.getWorldObj().getTileEntity(te.xCoord, te.yCoord + 1,te.zCoord);
			if (temp instanceof IConductor){
				result.add(temp);
			}else if (temp instanceof IEnergyTile){
				if(((IEnergyTile)temp).getFunctionalSide().getOpposite()==ForgeDirection.UP)
					result.add(temp);
			}
			
			temp = te.getWorldObj().getTileEntity(te.xCoord, te.yCoord - 1,te.zCoord);
			if (temp instanceof IConductor){
				result.add(temp);
			}else if (temp instanceof IEnergyTile){
				if(((IEnergyTile)temp).getFunctionalSide().getOpposite()==ForgeDirection.DOWN)
					result.add(temp);
			}
			
			temp = te.getWorldObj().getTileEntity(te.xCoord, te.yCoord,te.zCoord + 1);
			if (temp instanceof IConductor){
				result.add(temp);
			}else if (temp instanceof IEnergyTile){
				if(((IEnergyTile)temp).getFunctionalSide().getOpposite()==ForgeDirection.SOUTH)
					result.add(temp);
			}
			
			temp = te.getWorldObj().getTileEntity(te.xCoord, te.yCoord,	te.zCoord - 1);
			if (temp instanceof IConductor){
				result.add(temp);
			}else if (temp instanceof IEnergyTile){
				if(((IEnergyTile)temp).getFunctionalSide().getOpposite()==ForgeDirection.NORTH)
					result.add(temp);
			}
		}

		
		if (te instanceof IEnergyTile){		
			ForgeDirection myDirection=((IEnergyTile)te).getFunctionalSide();
			int x=0,y=0,z=0;
			
			switch(myDirection){
			case EAST:
				x++;
				break;
			case WEST:
				x--;
				break;
			case UP:
				y++;
				break;
			case DOWN:
				y--;
				break;
			case SOUTH:
				z++;
				break;
			case NORTH:
				z--;
				break;
			default:
				break;
			}
			
			temp = te.getWorldObj().getTileEntity(te.xCoord+x, te.yCoord+y,te.zCoord+z);	
			
			if (temp instanceof IConductor){
				result.add(temp);
			}
		}
		
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
		else if(te instanceof IConductor)
			return te.getResistance()/2;
		else
			return 0;
	}
	/*End of Simulator*/
	
	
	
	/** Called in each tick to attempt to do calculation*/
	public static void onTick(World world) {
		EnergyNet energyNet = getForWorld(world);
		if(energyNet.calc == true){
			energyNet.calc = false;
			energyNet.runSimulator();
			
			try{
				for (IBaseComponent tile:energyNet.tileEntityGraph.vertexSet()){
					if(tile instanceof IEnergyTile){
						IEnergyTile te=(IEnergyTile) tile;
						if(te.getMaxSafeVoltage()!=0&&te.getMaxSafeVoltage()<energyNet.voltageCache.get(tile))
							te.onOverVoltage(); //Over voltage check
					}
				}	
			}
			catch(Exception e){}
		}
	}
	
	/** Add a TileEntity to the energynet*/
	public void addTileEntity(TileEntity te) {
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
	}

	/** Remove a TileEntiy from the energy net*/
	public void removeTileEntity(TileEntity te) {
		tileEntityGraph.removeVertex((IBaseComponent) te);
		
		calc = true;
	}

	/** Refresh a node information for a tile which ALREADY attached to the energy network */
	public void rejoinTileEntity(TileEntity te){
		removeTileEntity(te);
		addTileEntity(te);
	}
	
	/** Mark the energy net for updating in next tick*/
	public void markForUpdate(TileEntity te){
		calc = true;
	}
	
	/** Return a instance of energynet for a specific world*/
	public static EnergyNet getForWorld(World world) {
		WorldData worldData = WorldData.get(world);
		return worldData.energyNet;
	}

	/** Creation of the energy network*/
	public EnergyNet() {
		System.out.println("EnergyNet create");
	}
}
