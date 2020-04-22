/**
 * This source code contains code pieces from Lambda Innovation
 */
package simelectricity.energynet;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraftforge.common.util.Constants.NBT;
import simelectricity.SimElectricity;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.node.ISESubComponent;
import simelectricity.api.tile.ISECableTile;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.api.tile.ISETile;
import simelectricity.api.tile.ISEWireTile;
import simelectricity.common.SELogger;
import simelectricity.energynet.components.Cable;
import simelectricity.energynet.components.GridNode;
import simelectricity.energynet.components.SEComponent;
import simelectricity.energynet.components.SEComponent.Tile;
import simelectricity.energynet.components.Wire;

import java.util.*;

public class EnergyNetDataProvider extends WorldSavedData {
    private static final String DATA_NAME = SimElectricity.MODID + "_GridData";
    //-----------------------------------------------------------------------------------------------------------
    ///////////////////////////////////////
    /// GridTile update notification
    ///////////////////////////////////////
    private final Set<ISEGridTile> updatedGridTile = new HashSet();
    //Map between coord and GridNode
    private final HashMap<BlockPos, GridNode> gridNodeMap = new HashMap<>();
    private final List<TileEntity> loadedTiles = new LinkedList<>();
    //Records TE that associated with grid nodes
    private final List<TileEntity> loadedGridTiles = new LinkedList<>();
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
        DimensionSavedDataManager storage = ((ServerWorld)world).getSavedData();
        EnergyNetDataProvider instance = storage.getOrCreate(EnergyNetDataProvider::new, EnergyNetDataProvider.DATA_NAME);

        if (instance == null) {
            instance = new EnergyNetDataProvider();
            storage.set(instance);
        }
        return instance;
    }


    //Tile Event handling ----------------------------------------------------------------------------

    //Utils ------------------------------------------------------------------------------
    public static TileEntity getTileEntityOnDirection(TileEntity te, Direction direction) {
        return te.getWorld().getTileEntity(te.getPos().offset(direction));
    }

    public static TileEntity getTileEntityOnDirection(TileEntity te, Direction direction, Direction direction2) {
        return te.getWorld().getTileEntity(te.getPos().offset(direction).offset(direction2));
    }

    public void onNewResultAvailable() {
        this.tileEntityGraph.updateVoltageCache();

        //Execute Handlers
        Iterator<TileEntity> iterator = this.loadedTiles.iterator();
        while (iterator.hasNext()) {
            TileEntity te = iterator.next();
            if (te instanceof ISEEnergyNetUpdateHandler)
                ((ISEEnergyNetUpdateHandler) te).onEnergyNetUpdate();
        }
        iterator = this.loadedGridTiles.iterator();
        while (iterator.hasNext()) {
            TileEntity te = iterator.next();
            if (te instanceof ISEEnergyNetUpdateHandler)
                ((ISEEnergyNetUpdateHandler) te).onEnergyNetUpdate();
        }
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
                
                for (Direction direction : Direction.values()) {
                    ISESubComponent subComponent = tile.getComponent(direction);
                    if (subComponent != null) {
                        this.tileEntityGraph.addVertex((SEComponent) subComponent);
                    }
                }
            }
            this.loadedTiles.add(te);
        }
    }

    private static boolean isSideSolid(World world, BlockPos pos, Direction facing) {
        return world.getBlockState(pos).isSolidSide(world, pos, facing);
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
            for (Direction direction : Direction.values()) {
                TileEntity neighborTileEntity = getTileEntityOnDirection(te, direction);

                if (!cable.canConnectOnSide(direction))
                	continue;
                
                if (neighborTileEntity instanceof ISECableTile) {  //Conductor
                    ISECableTile neighborCableTile = (ISECableTile) neighborTileEntity;
                    Cable neighborCable = (Cable) neighborCableTile.getNode();
                    
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
                            neighborCable.getColor() == 0 ||
                             cableTile.getColor() == neighborCable.getColor()
                            ) && 
                            cable.canConnectOnSide(direction) &&
                            neighborCable.canConnectOnSide(direction.getOpposite())) {

                        this.tileEntityGraph.addEdge(neighborCable, cable);
                    }
                } else if (neighborTileEntity instanceof ISETile) {
                    ISETile tile = (ISETile) neighborTileEntity;
                    ISESubComponent component = tile.getComponent(direction.getOpposite());

                    if (component != null) {
                        this.tileEntityGraph.addEdge((SEComponent) component, cable);
                    }
                }
            }
        } else if (te instanceof ISETile) {
            ISETile tile = (ISETile) te;

            for (Direction direction : Direction.values()) {
                ISESubComponent subComponent = tile.getComponent(direction);
                if (subComponent != null) {
                    this.tileEntityGraph.isolateVertex((SEComponent) subComponent);
                }
            }

            if (te instanceof ISEWireTile) {
                ISEWireTile wireTile = (ISEWireTile) te;
                for (Direction side : Direction.values()) {
                    Wire wire = (Wire) wireTile.getComponent(side);

                    // Solve connections with cable and ISETile
                    if (wire.hasBranchOnSide(null)) {
                        TileEntity neighborTileEntity = getTileEntityOnDirection(te, side);
                        if (neighborTileEntity instanceof ISECableTile) {
                            Cable cable = (Cable) ((ISECableTile) neighborTileEntity).getNode();
                            // Connected properly
                            if (cable.canConnectOnSide(side.getOpposite()))
                                this.tileEntityGraph.addEdge(cable, wire);
                        } else if (neighborTileEntity instanceof ISETile && !(neighborTileEntity instanceof ISEWireTile)) {
                            this.tileEntityGraph.addEdge((SEComponent) ((ISETile) neighborTileEntity).getComponent(side.getOpposite()), wire);
                        }
                    }

                    for (Direction branch: Direction.values()) {
                        if (branch.getAxis() == side.getAxis())
                            continue;

                        if (!wire.hasBranchOnSide(branch))
                            continue;

                        Wire wireNeighbor = (Wire) wireTile.getComponent(branch);
                        // Connections within the ISEWireTile
                        if (wireNeighbor.hasBranchOnSide(side))
                            this.tileEntityGraph.addEdge(wire, wireNeighbor);

                        // Co-planar Connections
                        TileEntity neighborTileEntity = getTileEntityOnDirection(te, branch);
                        if (neighborTileEntity instanceof ISEWireTile) {
                            wireNeighbor = (Wire) ((ISEWireTile) neighborTileEntity).getComponent(side);
                            if (wireNeighbor.hasBranchOnSide(branch.getOpposite()))
                                this.tileEntityGraph.addEdge(wire, wireNeighbor);
                        }

                        // Corner connections
                        neighborTileEntity = getTileEntityOnDirection(te, side, branch);
                        if (neighborTileEntity instanceof ISEWireTile) {
                            wireNeighbor = (Wire) ((ISEWireTile) neighborTileEntity).getComponent(branch.getOpposite());
                            if (wireNeighbor.hasBranchOnSide(side.getOpposite()) &&
                                    !isSideSolid(te.getWorld(), te.getPos().offset(branch), side) &&
                                    !isSideSolid(te.getWorld(), te.getPos().offset(branch), branch.getOpposite()))
                                this.tileEntityGraph.addEdge(wire, wireNeighbor);
                        }
                    }
                }
            } else {
                // ISETile
                for (Direction direction : Direction.values()) {
                    ISESubComponent subComponent = tile.getComponent(direction);
                    if (subComponent != null) {
                        TileEntity neighborTileEntity = getTileEntityOnDirection(te, direction);

                        if (neighborTileEntity instanceof ISECableTile) {
                            Cable cable = (Cable) ((ISECableTile) neighborTileEntity).getNode();
                            // Connected properly
                            if (cable.canConnectOnSide(direction.getOpposite()))
                                this.tileEntityGraph.addEdge(cable, (SEComponent) subComponent);
                        } else if (neighborTileEntity instanceof ISEWireTile) {
                            Wire wire = (Wire) ((ISEWireTile) neighborTileEntity).getComponent(direction.getOpposite());
                            if (wire.hasBranchOnSide(null))
                                this.tileEntityGraph.addEdge(wire, (SEComponent) subComponent);
                        }
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
            for (Direction direction : Direction.values()) {
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
            for (Direction direction : Direction.values()) {
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
    	if (!this.loadedTiles.contains(te))
            return;
    	
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
    public void read(CompoundNBT nbt) {
        this.gridNodeMap.clear();

        ListNBT NBTObjects = nbt.getList("Objects", NBT.TAG_COMPOUND);
        for (int i = 0; i < NBTObjects.size(); i++) {
            CompoundNBT compound = NBTObjects.getCompound(i);
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
    public CompoundNBT write(CompoundNBT nbt) {
        ListNBT NBTNodes = new ListNBT();
        for (GridNode gridNode : this.gridNodeMap.values()) {
            CompoundNBT tag = new CompoundNBT();
            gridNode.writeToNBT(tag);
            NBTNodes.add(tag);
        }
        nbt.put("Objects", NBTNodes);

        return nbt;
    }

    public void fireGridTileUpdateEvent() {
        for (ISEGridTile gridTile : this.updatedGridTile) {
            gridTile.onGridNeighborUpdated();
        }
        this.updatedGridTile.clear();
    }
}
