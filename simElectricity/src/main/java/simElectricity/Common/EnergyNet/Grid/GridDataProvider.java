/**
 * This source code contains code pieces from Lambda Innovation
 */
package simElectricity.Common.EnergyNet.Grid;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import simElectricity.API.Util;
import simElectricity.Common.EnergyNet.BakaGraph;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.Constants;

public class GridDataProvider extends WorldSavedData{
	private static final String DATA_NAME = Util.MODID + "_GridData";

	private HashMap<String, GridObject> gridObjectMap = new HashMap<String, GridObject>();
	
	private BakaGraph<GridObject> gridObjects = new BakaGraph<GridObject>();

	public int getGridObjectCount(){
		return gridObjectMap.size();
	}
	
	public GridObject getGridObjectAtCoord(int x, int y, int z){
		return gridObjectMap.get(GridObject.getIDString(x, y, z));
	}
	
	public GridObject addGridObject(int x, int y, int z, byte type){
		GridObject obj = null;
		if (type == 1){
			obj = new GridNode(this);
		}
		obj.x = x;
		obj.y = y;
		obj.z = z;
		obj.type = type;
		obj.gridDataProvider = this;
		gridObjects.addVertex(obj);
		gridObjectMap.put(obj.getIDString(), obj);
		
		this.markDirty();
		return obj;
	}
	
	public void removeGridObject(GridObject gridObject){
		for (GridObject neighbor : gridObjects.neighborListOf(gridObject)){
			neighbor.resistances.remove(gridObject);
		}
		
		gridObjects.removeVertex(gridObject);
		gridObjectMap.remove(gridObject.getIDString());
		this.markDirty();
	}
	
	public void addConnection(GridObject node1, GridObject node2, double resistance){
		gridObjects.addEdge(node1, node2);
		node1.resistances.put(node2, resistance);
		node2.resistances.put(node1, resistance);
		this.markDirty();
	}
	
	public void removeConnection(GridObject node1, GridObject node2){
		node1.resistances.remove(node2);
		node2.resistances.remove(node1);
		gridObjects.removeEdge(node1, node2);
		this.markDirty();
	}

	
	
	
	
	
	//-----------------------------------------------------------------------------------------------------------
	
	
	// Required constructors
	public GridDataProvider() {
		super(DATA_NAME);
	}
	  
	public GridDataProvider(String s) {
	    super(s);
	}
	
	public static GridDataProvider get(World world) {
		if(world.isRemote)
			throw new RuntimeException("Not allowed to create WiWorldData in client"); 
		
		// The IS_GLOBAL constant is there for clarity, and should be simplified into the right branch.
		MapStorage storage = world.perWorldStorage;
		GridDataProvider instance = (GridDataProvider) storage.loadData(GridDataProvider.class, DATA_NAME);

		if (instance == null) {
		  instance = new GridDataProvider();
		  storage.setData(DATA_NAME, instance);
		}
		return instance;
	}
			
	@Override
	public void readFromNBT(NBTTagCompound nbt) {	
		gridObjectMap.clear();
		gridObjects.clear();
		
		NBTTagList NBTObjects = nbt.getTagList("Objects", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < NBTObjects.tagCount(); i++) {
			NBTTagCompound compound = NBTObjects.getCompoundTagAt(i);
			GridObject obj = null;		
			byte type = compound.getByte("type");
			
			if (type == 1){
				obj = new GridNode(this);
			}
			
			if (obj == null){
				throw new RuntimeException("Undefined grid object type");
			}
			obj.readFromNBT(compound);
			gridObjects.addVertex(obj);
			
			gridObjectMap.put(obj.getIDString(), obj);
		}		
		

		//Build node connections
		for (GridObject gridObject : gridObjects.vertexSet()){
			gridObject.buildNeighborConnection(gridObjectMap);
		}
		
	}

	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {	
		NBTTagList NBTNodes = new NBTTagList();
		for (GridObject gridObj : gridObjects.vertexSet()){
			NBTTagCompound tag = new NBTTagCompound();
			LinkedList<GridObject> neighbors = gridObjects.neighborListOf(gridObj);
			gridObj.writeToNBT(tag, neighbors);
			NBTNodes.appendTag(tag);
		}
		nbt.setTag("Objects", NBTNodes);
	}
}
