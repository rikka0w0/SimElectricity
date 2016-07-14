package simElectricity.Common.EnergyNet.Grid;

import java.util.HashMap;
import java.util.LinkedList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import simElectricity.API.EnergyTile.ISESimulatable;

public abstract class GridObject implements ISESimulatable{
	public GridDataProvider gridDataProvider;
	
	public byte type;
	public int x;
	public int y;
	public int z;
	
	public HashMap<GridObject, Double> resistances = new HashMap<GridObject, Double>();
	
	
	//Only used during loading
	protected int neighborX[];
	protected int neighborY[];
	protected int neighborZ[];
	private double[] resistancesBuf;
		
	public GridObject(GridDataProvider dataProvider){
		gridDataProvider = dataProvider;
	}
	
	public int buildNeighborConnection(HashMap<String, GridObject> gridObjectMap){
		int numOfNeighbors = neighborX.length;
		
		for (int i = 0; i<numOfNeighbors ; i++){
			String neighborID = getIDString(neighborX[i], neighborY[i], neighborZ[i]);
			GridObject neighbor = gridObjectMap.get(neighborID);
			gridDataProvider.addConnection(this, neighbor, resistancesBuf[i]);
		}
		
		return numOfNeighbors;
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		type = nbt.getByte("type");
		x = nbt.getInteger("x");
		y = nbt.getInteger("y");
		z = nbt.getInteger("z");
		neighborX = nbt.getIntArray("neigborX");
		neighborY = nbt.getIntArray("neigborY");
		neighborZ = nbt.getIntArray("neigborZ");
		
		
		int numOfNeighbors = neighborX.length;
		resistancesBuf = new double[numOfNeighbors];
		for (int i=0; i<numOfNeighbors; i++){
			resistancesBuf[i] = nbt.getDouble("R"+String.valueOf(i));
		}
	}
	
	public void writeToNBT(NBTTagCompound nbt, LinkedList<GridObject> neighbors) {	
		nbt.setByte("type", type);
		nbt.setInteger("x", x);
		nbt.setInteger("y", y);
		nbt.setInteger("z", z);
		
		
		neighborX = new int[neighbors.size()];
		neighborY = new int[neighbors.size()];
		neighborZ = new int[neighbors.size()];
		resistancesBuf = new double[neighbors.size()];
		int i = 0;
		for (GridObject gridObject : neighbors){
			neighborX[i] = gridObject.x;
			neighborY[i] = gridObject.y;
			neighborZ[i] = gridObject.z;
			nbt.setDouble("R"+String.valueOf(i), resistancesBuf[i]);
			i++;
		}
		nbt.setIntArray("neigborX", neighborX);
		nbt.setIntArray("neigborY", neighborY);
		nbt.setIntArray("neigborZ", neighborZ);
	}
	
	public String getIDString(){
		return getIDString(x, y, z);
	}
	
	public static String getIDString(int x, int y, int z){
		return String.valueOf(x) + ":" +String.valueOf(y) + ":" + String.valueOf(z);
	}
}
