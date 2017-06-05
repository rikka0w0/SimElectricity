/**
 * This source code contains code pieces from Lambda Innovation
 */
package simelectricity.energynet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import simelectricity.api.node.ISESubComponent;
import simelectricity.api.tile.ISECableTile;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.api.tile.ISETile;
import simelectricity.common.SEUtils;
import simelectricity.energynet.components.Cable;
import simelectricity.energynet.components.GridNode;
import simelectricity.energynet.components.RegulatorInput;
import simelectricity.energynet.components.SEComponent;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public class EnergyNetDataProvider extends WorldSavedData{
	private static final String DATA_NAME = SEUtils.MODID + "_GridData";
	
	//Map between coord and GridNode
	private HashMap<String, GridNode> gridNodeMap = new HashMap<String, GridNode>();

	private List<TileEntity> loadedTiles = new LinkedList<TileEntity>();
	//Records TE that associated with grid nodes
	private List<TileEntity> loadedGridTiles = new LinkedList<TileEntity>();
	
	//Records the connection between components
	private SEGraph tileEntityGraph = new SEGraph();


	//Utils ------------------------------------------------------------------------------
	public Iterator<TileEntity> getLoadedTileIterator(){
		return loadedTiles.iterator();
	}
	
	public Iterator<TileEntity> getLoadedGridTileIterator(){
		return loadedGridTiles.iterator();
	}
	
	public int getGridObjectCount(){
		return gridNodeMap.size();
	}
	
	public GridNode getGridObjectAtCoord(int x, int y, int z){
		return gridNodeMap.get(GridNode.getIDString(x, y, z));
	}
	
	
	//Tile Event handling ----------------------------------------------------------------------------	
	/**
	 * @param te must implements ISECableTile or ISETile
	 */
	public void registerTile(TileEntity te){
		if (loadedTiles.contains(te)){
			SEUtils.logWarn("Duplicated TileEntity:" + te.toString() +", this could be a bug!", SEUtils.energyNet);
		}else{
            loadedTiles.add(te);
		}
		
		if (te instanceof ISECableTile){
			ISECableTile cableTile = (ISECableTile) te;
			Cable cable = (Cable) cableTile.getNode();
        	tileEntityGraph.addVertex(cable);
        	
        	if (cable.isGridInterConnectionPoint){
        		GridNode gridNode = this.getGridObjectAtCoord(te.xCoord, te.yCoord, te.zCoord);
        		tileEntityGraph.interconnection(cable, gridNode);
        	}
        	
        	//Build connection with neighbors
            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            	TileEntity neighborTileEntity = SEUtils.getTileEntityOnDirection(te, direction);
            	
                if (neighborTileEntity instanceof ISECableTile) {  //Conductor
                	ISECableTile neighborCableTile = (ISECableTile) neighborTileEntity;

                	/*
                	 * Two cable blocks can link together if and only if both of the following conditions are meet:
                	 * 
                	 * Condition 1:
                	 * One of the cable's color is 0
                	 * OR
                	 * They have the same color
                	 * 
                	 * Condition 2:
                	 * Both cableA and cableB return true in their canConnectOnSide
                	 */
                    if (
                    	(	cableTile.getColor() == 0 ||
                    			neighborCableTile.getColor() == 0 ||
                         	cableTile.getColor() == neighborCableTile.getColor()
                    	)&&(
                    		cableTile.canConnectOnSide(direction) &&
                    		neighborCableTile.canConnectOnSide(direction.getOpposite())
                    	)) {
                    	
                        tileEntityGraph.addEdge((SEComponent) neighborCableTile.getNode(), (SEComponent) cableTile.getNode());
                    }
                }
                else if (neighborTileEntity instanceof ISETile) {
                	ISETile tile = (ISETile)neighborTileEntity;
                	ISESubComponent component = tile.getComponent(direction.getOpposite());
                	
                	if (component != null){
                		tileEntityGraph.addEdge((SEComponent) component, (SEComponent) cableTile.getNode());
                	}
                }
            }    
        }
        else if (te instanceof ISETile){
        	ISETile tile = (ISETile)te;
        	for (ForgeDirection direction : tile.getValidDirections()) {
        		ISESubComponent subComponent = tile.getComponent(direction);
        		tileEntityGraph.addVertex((SEComponent) subComponent);
        		
    			TileEntity neighborTileEntity = SEUtils.getTileEntityOnDirection(te, direction);
                
                if (neighborTileEntity instanceof ISECableTile){
                	// Connected properly
                	if (((Cable)((ISECableTile)neighborTileEntity).getNode()).canConnectOnSide(direction.getOpposite()))
                		tileEntityGraph.addEdge((SEComponent) ((ISECableTile)neighborTileEntity).getNode(),(SEComponent)  subComponent);
                }
                
                
                //Also don`t forget to attach the regulator controller to the energyNet!
                if (subComponent instanceof RegulatorInput)
                	tileEntityGraph.addVertex(((RegulatorInput)subComponent).controller);	
        	}
        }
        else{
        	throw new RuntimeException("Unexpected TileEntity:" + te.toString());
        }
	}
	
	public void unregisterTile(TileEntity te){
		if (loadedTiles.contains(te)){
			loadedTiles.remove(te);
		}else{
			SEUtils.logWarn("Attempt to remove unregistered:" + te.toString() +", this could be a bug!", SEUtils.energyNet);
		}
		
        if (te instanceof ISECableTile) {
        	Cable cable = (Cable) ((ISECableTile) te).getNode();       	
        	
        	tileEntityGraph.removeVertex(cable);
        }
	    else if (te instanceof ISETile){
        	ISETile tile = (ISETile)te;
        	for (ForgeDirection direction : tile.getValidDirections()) {
        		ISESubComponent subComponent = tile.getComponent(direction);
        		tileEntityGraph.removeVertex((SEComponent) subComponent);
        		
        		if (subComponent instanceof RegulatorInput)
                	tileEntityGraph.removeVertex(((RegulatorInput)subComponent).controller);
        	}
	    }else{
        	throw new RuntimeException("Unexpected TileEntity:" + te.toString());
        }
	}

	public void updateTileParam(TileEntity te){
        if (te instanceof ISECableTile) {
        	Cable cable = (Cable)((ISECableTile) te).getNode();
        	cable.updateComponentParameters();
        }
	    else if (te instanceof ISETile){
        	ISETile tile = (ISETile)te;
        	for (ForgeDirection direction : tile.getValidDirections()) {
        		ISESubComponent subComponent = tile.getComponent(direction);
        		
        		if (subComponent instanceof SEComponent.Tile)
        			((SEComponent.Tile)subComponent).updateComponentParameters();
        	}
	    }else{
        	throw new RuntimeException("Unexpected TileEntity:" + te.toString());
        }	
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
	///////////////////////////////////////
	///PerWorldStorage
	///////////////////////////////////////
	
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

	/**
	 * Read grid data from world NBT, 1.Read grid nodes, 2. Read connections
	 */
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
		
		SEUtils.logInfo("Grid objectes has been loaded", SEUtils.energyNet);
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
