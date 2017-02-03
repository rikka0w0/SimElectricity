/**
 * This source code contains code pieces from Lambda Innovation
 */
package simElectricity.Common.EnergyNet;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import simElectricity.API.EnergyTile.ISEGridNode;
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.Tile.ISEGridTile;
import simElectricity.Common.SEUtils;
import simElectricity.Common.EnergyNet.Components.GridNode;
import simElectricity.Common.EnergyNet.Components.SEComponent;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.Constants;

public class EnergyNetDataProvider extends WorldSavedData{
	private static final String DATA_NAME = SEUtils.MODID + "_GridData";
	
	//Stores a list of grid nodes
	private HashMap<String, GridNode> gridNodeMap = new HashMap<String, GridNode>();

	//Records TE that associated with grid nodes
	private List<TileEntity> loadedGridTiles = new LinkedList<TileEntity>();
	
	//Records the connection between components
	private SEGraph tileEntityGraph = new SEGraph();


	//Utils ------------------------------------------------------------------------------

	
	public int getGridObjectCount(){
		return gridNodeMap.size();
	}
	
	public GridNode getGridObjectAtCoord(int x, int y, int z){
		return gridNodeMap.get(GridNode.getIDString(x, y, z));
	}
	
	
	//Grid Event handling ----------------------------------------------------------------------------
	public GridNode addGridNode(int x, int y, int z, byte type){
		GridNode obj = new GridNode(this);
		
		obj.x = x;
		obj.y = y;
		obj.z = z;
		obj.type = type;
		obj.gridDataProvider = this;
		tileEntityGraph.addVertex(obj);
		gridNodeMap.put(obj.getIDString(), obj);
		
		this.markDirty();
		return obj;
	}
	
	public void removeGridNode(GridNode gridNode){
		for (GridNode affectedNeighbors: tileEntityGraph.removeGridVertex(gridNode)){
			TileEntity te = affectedNeighbors.te;
			if (te instanceof ISEGridTile)
				((ISEGridTile)te).onGridNeighborUpdated();
		}
		
		gridNodeMap.remove(gridNode.getIDString());
		this.markDirty();
	}
	
	public void addGridConnection(GridNode node1, GridNode node2, double resistance){
		tileEntityGraph.addGridEdge(node1, node2, resistance);
		
		TileEntity te1 = node1.te;
		TileEntity te2 = node2.te;
		
		if (te1 instanceof ISEGridTile)
			((ISEGridTile)te1).onGridNeighborUpdated();
		if (te2 instanceof ISEGridTile)
			((ISEGridTile)te2).onGridNeighborUpdated();
		
		this.markDirty();
	}
	
	public void removeGridConnection(GridNode node1, GridNode node2){
		tileEntityGraph.removeGridEdge(node1, node2);
		
		TileEntity te1 = node1.te;
		TileEntity te2 = node2.te;
		
		if (te1 instanceof ISEGridTile)
			((ISEGridTile)te1).onGridNeighborUpdated();
		if (te2 instanceof ISEGridTile)
			((ISEGridTile)te2).onGridNeighborUpdated();
		
		this.markDirty();
	}

	public void onGridTilePresent(TileEntity te){
		ISEGridTile gridTile = (ISEGridTile)te;
		GridNode gridObject = gridNodeMap.get(GridNode.getIDStringFromTileEntity(te));
		if (gridObject == null)
			return;
		loadedGridTiles.add(te);
		gridObject.te = te;
		gridTile.setGridNode(gridObject);
		gridTile.onGridNeighborUpdated();
	}
	
	public void onGridTileInvalidate(TileEntity te){
		loadedGridTiles.remove(te);
		
		ISEGridTile gridTile = (ISEGridTile)te;
		GridNode gridObject = gridNodeMap.get(GridNode.getIDStringFromTileEntity(te));
		
		//gridObject can be null if the GridObject is just removed
		if (gridObject != null)
			gridObject.te = null;
	}
	
	
	
	
	
	
	//-----------------------------------------------------------------------------------------------------------
	
	
	// Required constructors
	public EnergyNetDataProvider() {
		super(DATA_NAME);
	}
	  
	public EnergyNetDataProvider(String s) {
	    super(s);
	}
	
	public static EnergyNetDataProvider get(World world) {
		if(world.isRemote)
			throw new RuntimeException("Not allowed to create WiWorldData in client"); 
		
		// The IS_GLOBAL constant is there for clarity, and should be simplified into the right branch.
		MapStorage storage = world.perWorldStorage;
		EnergyNetDataProvider instance = (EnergyNetDataProvider) storage.loadData(EnergyNetDataProvider.class, DATA_NAME);

		if (instance == null) {
		  instance = new EnergyNetDataProvider();
		  storage.setData(DATA_NAME, instance);
		}
		return instance;
	}
	
	public SEGraph getTEGraph(){
		return tileEntityGraph;
	}
			
	@Override
	public void readFromNBT(NBTTagCompound nbt) {	
		gridNodeMap.clear();
		//gridObjects.clear();
		
		NBTTagList NBTObjects = nbt.getTagList("Objects", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < NBTObjects.tagCount(); i++) {
			NBTTagCompound compound = NBTObjects.getCompoundTagAt(i);
			GridNode obj = new GridNode(this);	
			byte type = compound.getByte("type");
			

				//throw new RuntimeException("Undefined grid object type");

			obj.readFromNBT(compound);
			tileEntityGraph.addVertex(obj);
			
			gridNodeMap.put(obj.getIDString(), obj);
		}		
		

		//Build node connections
		for (GridNode gridNode : gridNodeMap.values()){
			gridNode.buildNeighborConnection(gridNodeMap);
		}
	}

	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {	
		NBTTagList NBTNodes = new NBTTagList();
		for (GridNode gridNode : gridNodeMap.values()){
			NBTTagCompound tag = new NBTTagCompound();
			gridNode.writeToNBT(tag);
			NBTNodes.appendTag(tag);
		}
		nbt.setTag("Objects", NBTNodes);
	}
}
