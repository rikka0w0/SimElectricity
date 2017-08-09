package simelectricity.energynet.components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import simelectricity.api.node.ISEGridNode;
import simelectricity.api.node.ISESimulatable;
import simelectricity.energynet.SEGraph;

public class GridNode extends SEComponent implements ISEGridNode{
	private final BlockPos pos;
	
	//0 - transmission line 1 - transformer primary 2 - transformer secondary
	public byte type;
	//Transformer secondary/primary
	public GridNode complement;
	public double ratio, resistance;
	
	//Only stores resistances between GridNodes!
	public LinkedList<Double> neighborR = new LinkedList<Double>();
	
	//Simulation & Optimization
	public Cable interConnection = null;;
	
	//Only used for loading
	private int[] neighborX;
	private int[] neighborY;
	private int[] neighborZ;
	private double[] resistancesBuf;
	private int complementX, complementY, complementZ;
		
	public GridNode(BlockPos pos){
		this.pos = pos;
		this.type = ISEGridNode.ISEGridNode_Wire;
	}
	
	///////////////////////
	/// Read from NBT
	///////////////////////
	
	public GridNode(NBTTagCompound nbt){
		this.pos = new BlockPos(
				nbt.getInteger("x"),
				nbt.getInteger("y"),
				nbt.getInteger("z")
				);
		this.type = nbt.getByte("type");
		this.neighborX = nbt.getIntArray("neigborX");
		this.neighborY = nbt.getIntArray("neigborY");
		this.neighborZ = nbt.getIntArray("neigborZ");
		
		this.complementY = nbt.getInteger("complementY");
		if (complementY>0){
			this.complementX = nbt.getInteger("complementX");
			this.complementZ = nbt.getInteger("complementZ");
			this.ratio = nbt.getDouble("ratio");
			this.resistance = nbt.getDouble("resistance");
		}

		
		int numOfNeighbors = neighborX.length;
		this.resistancesBuf = new double[numOfNeighbors];
		for (int i=0; i<numOfNeighbors; i++){
			resistancesBuf[i] = nbt.getDouble("R"+String.valueOf(i));
		}
	}
	
	public void buildNeighborConnection(HashMap<BlockPos, GridNode> gridNodeMap, SEGraph graph){
		for (int i = 0; i<neighborX.length ; i++){
			GridNode neighbor = gridNodeMap.get(new BlockPos(neighborX[i], neighborY[i], neighborZ[i]));
			
			graph.addGridEdge(this, neighbor, resistancesBuf[i]);
		}
		
		this.complement = gridNodeMap.get(new BlockPos(complementX, complementY, complementZ));
	}
	
	///////////////////////
	/// Save to NBT
	///////////////////////
	public void writeToNBT(NBTTagCompound nbt) {	
		nbt.setByte("type", type);
		nbt.setInteger("x", pos.getX());
		nbt.setInteger("y", pos.getY());
		nbt.setInteger("z", pos.getZ());
		
		int length = 0;
		for (SEComponent neighbor : neighbors){
			if (neighbor instanceof GridNode)
				length++;
		}
		
		neighborX = new int[length];
		neighborY = new int[length];
		neighborZ = new int[length];
		int i = 0;
		Iterator<Double> iterator = neighborR.iterator();
		for (SEComponent neighbor : neighbors){
			if (neighbor instanceof GridNode){
				GridNode gridNode = (GridNode)neighbor;
				neighborX[i] = gridNode.pos.getX();
				neighborY[i] = gridNode.pos.getY();
				neighborZ[i] = gridNode.pos.getZ();
				nbt.setDouble("R"+String.valueOf(i), iterator.next());
				i++;
			}
		}
		nbt.setIntArray("neigborX", neighborX);
		nbt.setIntArray("neigborY", neighborY);
		nbt.setIntArray("neigborZ", neighborZ);
		
		if (complement != null){
			nbt.setInteger("complementX", complement.getPos().getX());
			nbt.setInteger("complementY", complement.getPos().getY());
			nbt.setInteger("complementZ", complement.getPos().getZ());
		}else{
			nbt.setInteger("complementY", -1);
		}
		nbt.setDouble("ratio", ratio);
		nbt.setDouble("resistance", resistance);
	}
	
	
	public double getResistance(GridNode neighbor) {
		Iterator<SEComponent> iterator1 = neighbors.iterator();
		Iterator<Double> iterator2 = neighborR.iterator();
		while(iterator1.hasNext()){
			SEComponent cur = iterator1.next();
			if (cur instanceof GridNode){
				double res = iterator2.next();
				if (cur == neighbor)
					return res;
			}
		}
		return Double.NaN;
	}
	
	//ISEGridObject -----------------------------
	@Override
	public LinkedList<ISESimulatable> getNeighborList(){
		LinkedList<ISESimulatable> ret = new LinkedList<ISESimulatable>();
		for (ISESimulatable obj : this.neighbors){
			ret.add(obj);
		}
		return ret;
	}
	
	@Override
	public BlockPos getPos(){
		return this.pos;
	}

	@Override
	public int getType() {
		return this.type;
	}
}
