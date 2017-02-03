package simElectricity.Common.EnergyNet.Components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

import simElectricity.API.DataProvider.ISEComponentDataProvider;
import simElectricity.API.EnergyTile.ISEGridNode;
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.EnergyTile.ISESubComponent;
import simElectricity.Common.EnergyNet.EnergyNetDataProvider;

public class GridNode extends SEComponent implements ISEGridNode{
	public EnergyNetDataProvider gridDataProvider;
	
	//0 - node 1 - transformer primary 2 - transformer secondary
	public byte type;
	public int x;
	public int y;
	public int z;
	
	public LinkedList<Double> neighborR = new LinkedList<Double>();
	
	//Only used for loading
	protected int neighborX[];
	protected int neighborY[];
	protected int neighborZ[];
	private double[] resistancesBuf;
		
	public GridNode(EnergyNetDataProvider dataProvider){
		gridDataProvider = dataProvider;
	}
	
	public double getResistance(GridNode neighbor) {
		Iterator<SEComponent> iterator1 = neighbors.iterator();
		Iterator<Double> iterator2 = neighborR.iterator();
		while(iterator1.hasNext()){
			double res = iterator2.next();
			if (iterator1.next() == neighbor)
				return res;
		}
		return Double.NaN;
	}
	
	public int buildNeighborConnection(HashMap<String, GridNode> gridNodeMap){
		int numOfNeighbors = neighborX.length;
		
		for (int i = 0; i<numOfNeighbors ; i++){
			String neighborID = getIDString(neighborX[i], neighborY[i], neighborZ[i]);
			GridNode neighbor = gridNodeMap.get(neighborID);
			
			gridDataProvider.getTEGraph().addGridEdge(this, neighbor, resistancesBuf[i]);
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

	@Override
	public ISEComponentDataProvider getDataProvider() {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public ISESubComponent getComplement() {
		return null;
	}
}
