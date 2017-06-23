package simelectricity.energynet.components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import simelectricity.api.node.ISEGridNode;
import simelectricity.api.node.ISESimulatable;
import simelectricity.energynet.SEGraph;

public class GridNode extends SEComponent implements ISEGridNode{
	//public EnergyNetDataProvider gridDataProvider;
	
	//0 - transmission line 1 - transformer primary 2 - transformer secondary
	private final byte type;
	private final int x;
	private final int y;
	private final int z;
	
	//Only stores resistances between GridNodes!
	public LinkedList<Double> neighborR = new LinkedList<Double>();
	
	//Simulation & Optimization
	public Cable interConnection = null;;
	
	//Only used for loading
	private int neighborX[];
	private int neighborY[];
	private int neighborZ[];
	private double[] resistancesBuf;
		
	public GridNode(int x, int y, int z, byte type){
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
	}
	
	///////////////////////
	/// Read from NBT
	///////////////////////
	
	public GridNode(NBTTagCompound nbt){
		this.x = nbt.getInteger("x");
		this.y = nbt.getInteger("y");
		this.z = nbt.getInteger("z");
		this.type = nbt.getByte("type");
		this.neighborX = nbt.getIntArray("neigborX");
		this.neighborY = nbt.getIntArray("neigborY");
		this.neighborZ = nbt.getIntArray("neigborZ");
		
		
		int numOfNeighbors = neighborX.length;
		this.resistancesBuf = new double[numOfNeighbors];
		for (int i=0; i<numOfNeighbors; i++){
			resistancesBuf[i] = nbt.getDouble("R"+String.valueOf(i));
		}
	}
	
	public void buildNeighborConnection(HashMap<String, GridNode> gridNodeMap, SEGraph graph){
		for (int i = 0; i<neighborX.length ; i++){
			String neighborID = getIDString(neighborX[i], neighborY[i], neighborZ[i]);
			GridNode neighbor = gridNodeMap.get(neighborID);
			
			graph.addGridEdge(this, neighbor, resistancesBuf[i]);
		}
	}
	
	///////////////////////
	/// Save to NBT
	///////////////////////
	public void writeToNBT(NBTTagCompound nbt) {	
		nbt.setByte("type", type);
		nbt.setInteger("x", x);
		nbt.setInteger("y", y);
		nbt.setInteger("z", z);
		
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
				neighborX[i] = gridNode.x;
				neighborY[i] = gridNode.y;
				neighborZ[i] = gridNode.z;
				nbt.setDouble("R"+String.valueOf(i), iterator.next());
				i++;
			}
		}
		nbt.setIntArray("neigborX", neighborX);
		nbt.setIntArray("neigborY", neighborY);
		nbt.setIntArray("neigborZ", neighborZ);
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
	
	public String getIDString(){
		return getIDString(x, y, z);
	}
	
	public static String getIDString(int x, int y, int z){
		return String.valueOf(x) + ":" +String.valueOf(y) + ":" + String.valueOf(z);
	}
	
	public static String getIDStringFromTileEntity(TileEntity te){
		return getIDString(te.xCoord, te.yCoord, te.zCoord);
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
	public int getXCoord(){
		return this.x;
	}
	
	@Override
	public int getYCoord(){
		return this.y;
	}
	
	@Override
	public int getZCoord(){
		return this.z;
	}

	@Override
	public int getType() {
		return this.type;
	}
}
