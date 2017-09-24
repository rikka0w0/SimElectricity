/**
 * This source code contains code pieces from Lambda Innovation
 */
package simelectricity.energynet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.Constants.NBT;
import simelectricity.SimElectricity;
import simelectricity.api.node.ISESubComponent;
import simelectricity.api.tile.ISECableTile;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.api.tile.ISETile;
import simelectricity.common.SELogger;
import simelectricity.energynet.components.Cable;
import simelectricity.energynet.components.GridNode;
import simelectricity.energynet.components.SEComponent;
import simelectricity.energynet.components.SEComponent.Tile;

import java.util.*;

public class EnergyNetDataProvider extends WorldSavedData {
    private static final String DATA_NAME = SimElectricity.MODID + "_GridData";
    //-----------------------------------------------------------------------------------------------------------
    ///////////////////////////////////////
    /// GridTile update notification
    ///////////////////////////////////////
    private final Set<ISEGridTile> updatedGridTile = new HashSet();
    //Map between coord and GridNode
    private final HashMap<BlockPos, GridNode> gridNodeMap = new HashMap<BlockPos, GridNode>();
    private final List<TileEntity> loadedTiles = new LinkedList<TileEntity>();
    //Records TE that associated with grid nodes
    private final List<TileEntity> loadedGridTiles = new LinkedList<TileEntity>();
    //Records the connection between components
    private final SEGraph tileEntityGraph = new SEGraph();

    // Required constructors
    public EnergyNetDataProvider() {
        super(EnergyNetDataProvider.DATA_NAME);
    }

    public EnergyNetDataProvider(String s) {
        super(s);
    }

    public static EnergyNetDataProvider get(World world) {
        if (world.isRemote)
            throw new RuntimeException("Not allowed to create WiWorldData in client");

        // The IS_GLOBAL constant is there for clarity, and should be simplified into the right branch.
        MapStorage storage = world.getPerWorldStorage();
        EnergyNetDataProvider instance = (EnergyNetDataProvider) storage.getOrLoadData(EnergyNetDataProvider.class, EnergyNetDataProvider.DATA_NAME);

        if (instance == null) {
            instance = new EnergyNetDataProvider();
            storage.setData(EnergyNetDataProvider.DATA_NAME, instance);
        }
        return instance;
    }


    //Tile Event handling ----------------------------------------------------------------------------

    //Utils ------------------------------------------------------------------------------
    public static TileEntity getTileEntityOnDirection(TileEntity te, EnumFacing direction) {
        return te.getWorld().getTileEntity(te.getPos().offset(direction));
    }
    
    public Iterator<TileEntity> getLoadedTileIterator() {
        return this.loadedTiles.iterator();
    }

    public Iterator<TileEntity> getLoadedGridTileIterator() {
        return this.loadedGridTiles.iterator();
    }

    public int getGridObjectCount() {
        return this.gridNodeMap.size();
    }

    public GridNode getGridObjectAtCoord(BlockPos pos) {
        return this.gridNodeMap.get(pos);
    }

    public void addTile(TileEntity te) {
        if (this.loadedTiles.contains(te)) {
            SELogger.logWarn(SELogger.energyNet, "Duplicated TileEntity:" + te + ", this could be a bug!");
        } else {
            if (te instanceof ISECableTile) {
                ISECableTile cableTile = (ISECableTile) te;
                Cable cable = (Cable) cableTile.getNode();
                this.tileEntityGraph.addVertex(cable);
            } if (te instanceof ISETile) {
                ISETile tile = (ISETile) te;
                
                for (EnumFacing direction : EnumFacing.VALUES) {
                    ISESubComponent subComponent = tile.getComponent(direction);
                    if (subComponent != null) {
                        this.tileEntityGraph.addVertex((SEComponent) subComponent);
                    }
                }
            }
            this.loadedTiles.add(te);
        }
    }

    public void updateTileConnection(TileEntity te) {
    	if (!this.loadedTiles.contains(te))
            return;

        if (te instanceof ISECableTile) {
            ISECableTile cableTile = (ISECableTile) te;
            Cable cable = (Cable) cableTile.getNode();
            
            this.tileEntityGraph.isolateVertex(cable);

            if (cable.isGridInterConnectionPoint) {
                GridNode gridNode = getGridObjectAtCoord(te.getPos());
                this.tileEntityGraph.interconnection(cable, gridNode);
            }

            //Build connection with neighbors
            for (EnumFacing direction : EnumFacing.VALUES) {
                TileEntity neighborTileEntity = getTileEntityOnDirection(te, direction);

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
                            (cableTile.getColor() == 0 ||
                                    neighborCableTile.getColor() == 0 ||
                                    cableTile.getColor() == neighborCableTile.getColor()
                            ) && cable.canConnectOnSide(direction) &&
                                    neighborCableTile.canConnectOnSide(direction.getOpposite())) {

                        this.tileEntityGraph.addEdge((SEComponent) neighborCableTile.getNode(), (SEComponent) cableTile.getNode());
                    }
                } else if (neighborTileEntity instanceof ISETile) {
                    ISETile tile = (ISETile) neighborTileEntity;
                    ISESubComponent component = tile.getComponent(direction.getOpposite());

                    if (component != null) {
                        this.tileEntityGraph.addEdge((SEComponent) component, (SEComponent) cableTile.getNode());
                    }
                }
            }
        } else if (te instanceof ISETile) {
            ISETile tile = (ISETile) te;
            
            for (EnumFacing direction : EnumFacing.VALUES) {
                ISESubComponent subComponent = tile.getComponent(direction);
                if (subComponent != null) {
                    this.tileEntityGraph.isolateVertex((SEComponent) subComponent);
                }
            }
            
            for (EnumFacing direction : EnumFacing.VALUES) {
                ISESubComponent subComponent = tile.getComponent(direction);
                if (subComponent != null) {
                    TileEntity neighborTileEntity = getTileEntityOnDirection(te, direction);

                    if (neighborTileEntity instanceof ISECableTile) {
                        // Connected properly
                        if (((Cable) ((ISECableTile) neighborTileEntity).getNode()).canConnectOnSide(direction.getOpposite()))
                            this.tileEntityGraph.addEdge((SEComponent) ((ISECableTile) neighborTileEntity).getNode(), (SEComponent) subComponent);
                    }
                }
            }
        } else {
            throw new RuntimeException("Unexpected TileEntity:" + te);
        }
    }
    
    public void updateTileParam(TileEntity te) {
    	if (!this.loadedTiles.contains(te))
            return;
    	
        if (te instanceof ISECableTile) {
            Cable cable = (Cable) ((ISECableTile) te).getNode();
            cable.updateComponentParameters();
        } else if (te instanceof ISETile) {
            ISETile tile = (ISETile) te;
            for (EnumFacing direction : EnumFacing.VALUES) {
                ISESubComponent subComponent = tile.getComponent(direction);

                if (subComponent instanceof Tile)
                    ((Tile) subComponent).updateComponentParameters();
            }
        } else {
            throw new RuntimeException("Unexpected TileEntity:" + te);
        }
    }
    
    public void removeTile(TileEntity te) {
    	if (!this.loadedTiles.contains(te))
            return;

        if (te instanceof ISECableTile) {
            Cable cable = (Cable) ((ISECableTile) te).getNode();

            this.tileEntityGraph.removeVertex(cable);
        } else if (te instanceof ISETile) {
            ISETile tile = (ISETile) te;
            for (EnumFacing direction : EnumFacing.VALUES) {
                ISESubComponent subComponent = tile.getComponent(direction);
                if (subComponent != null) {
                    this.tileEntityGraph.removeVertex((SEComponent) subComponent);
                }
            }
        } else {
            throw new RuntimeException("Unexpected TileEntity:" + te);
        }
        
        this.loadedTiles.remove(te);
    }

    //Grid Event handling ----------------------------------------------------------------------------
    public void addGridNode(GridNode gridNode) {
        this.tileEntityGraph.addVertex(gridNode);
        this.gridNodeMap.put(gridNode.getPos(), gridNode);

        markDirty();
    }

    public void removeGridNode(GridNode gridNode) {
        if (gridNode == null)
            return;            //TODO should we log this?

        for (GridNode affectedNeighbors : this.tileEntityGraph.removeGridVertex(gridNode)) {
            TileEntity te = affectedNeighbors.te;
            if (te instanceof ISEGridTile)
                this.updatedGridTile.add((ISEGridTile) te);
        }

        this.gridNodeMap.remove(gridNode.getPos());
        markDirty();
    }

    public void addGridConnection(GridNode node1, GridNode node2, double resistance) {
        if (node1 == null || node2 == null)
            return;

        this.tileEntityGraph.addGridEdge(node1, node2, resistance);

        TileEntity te1 = node1.te;
        TileEntity te2 = node2.te;

        if (te1 instanceof ISEGridTile)
            this.updatedGridTile.add((ISEGridTile) te1);
        if (te2 instanceof ISEGridTile)
            this.updatedGridTile.add((ISEGridTile) te2);

        markDirty();
    }

    public void removeGridConnection(GridNode node1, GridNode node2) {
        if (node1 == null || node2 == null)
            return;

        this.tileEntityGraph.removeGridEdge(node1, node2);

        TileEntity te1 = node1.te;
        TileEntity te2 = node2.te;

        if (te1 instanceof ISEGridTile)
            this.updatedGridTile.add((ISEGridTile) te1);
        if (te2 instanceof ISEGridTile)
            this.updatedGridTile.add((ISEGridTile) te2);

        markDirty();
    }


    //-----------------------------------------------------------------------------------------------------------
    ///////////////////////////////////////
    ///PerWorldStorage
    ///////////////////////////////////////

    public void makeTransformer(GridNode primary, GridNode secondary, double ratio, double resistance) {
        if (primary == null || secondary == null)
            return;

        this.tileEntityGraph.makeTransformer(primary, secondary, ratio, resistance);

        TileEntity te1 = primary.te;
        TileEntity te2 = secondary.te;

        if (te1 instanceof ISEGridTile)
            this.updatedGridTile.add((ISEGridTile) te1);
        if (te2 instanceof ISEGridTile)
            this.updatedGridTile.add((ISEGridTile) te2);

        markDirty();
    }

    public void breakTransformer(GridNode node) {
        if (node == null)
            return;

        this.tileEntityGraph.breakTransformer(node);

        TileEntity te = node.te;

        if (te instanceof ISEGridTile)
            this.updatedGridTile.add((ISEGridTile) te);

        markDirty();
    }

    public void onGridTilePresent(TileEntity te) {
        ISEGridTile gridTile = (ISEGridTile) te;
        GridNode gridObject = this.gridNodeMap.get(te.getPos());
        if (gridObject == null)
            return;
        this.loadedGridTiles.add(te);
        gridObject.te = te;
        gridTile.setGridNode(gridObject);
        this.updatedGridTile.add(gridTile);
    }

    public void onGridTileInvalidate(TileEntity te) {
        this.loadedGridTiles.remove(te);

        ISEGridTile gridTile = (ISEGridTile) te;
        GridNode gridObject = this.gridNodeMap.get(te.getPos());

        //gridObject can be null if the GridObject is just removed
        if (gridObject != null)
            gridObject.te = null;
    }

    public SEGraph getTEGraph() {
        return this.tileEntityGraph;
    }

    /**
     * Read grid data from world NBT, 1.Read grid nodes, 2. Read connections
     */
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.gridNodeMap.clear();

        NBTTagList NBTObjects = nbt.getTagList("Objects", NBT.TAG_COMPOUND);
        for (int i = 0; i < NBTObjects.tagCount(); i++) {
            NBTTagCompound compound = NBTObjects.getCompoundTagAt(i);
            GridNode obj = new GridNode(compound);
            //byte type = compound.getByte("type");


            //throw new RuntimeException("Undefined grid object type");

            //obj.readFromNBT(compound);
            this.tileEntityGraph.addVertex(obj);

            this.gridNodeMap.put(obj.getPos(), obj);
        }


        //Build node connections
        for (GridNode gridNode : this.gridNodeMap.values()) {
            gridNode.buildNeighborConnection(this.gridNodeMap, this.tileEntityGraph);
        }

        SELogger.logInfo(SELogger.energyNet, "Loaded GridNodes from world NBT storage");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTTagList NBTNodes = new NBTTagList();
        for (GridNode gridNode : this.gridNodeMap.values()) {
            NBTTagCompound tag = new NBTTagCompound();
            gridNode.writeToNBT(tag);
            NBTNodes.appendTag(tag);
        }
        nbt.setTag("Objects", NBTNodes);

        return nbt;
    }

    public void fireGridTileUpdateEvent() {
        for (ISEGridTile gridTile : this.updatedGridTile) {
            gridTile.onGridNeighborUpdated();
        }
        this.updatedGridTile.clear();
    }
}
