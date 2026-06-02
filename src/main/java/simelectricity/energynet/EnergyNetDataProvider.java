/**
 * This source code contains code pieces from Lambda Innovation
 */
package simelectricity.energynet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import simelectricity.SimElectricity;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.node.ISESubComponent;
import simelectricity.api.blockentity.ISECableBlockEntity;
import simelectricity.api.blockentity.ISEGridBlockEntity;
import simelectricity.api.blockentity.ISEBlockEntity;
import simelectricity.api.blockentity.ISEWireBlockEntity;
import simelectricity.common.SELogger;
import simelectricity.energynet.components.Cable;
import simelectricity.energynet.components.GridNode;
import simelectricity.energynet.components.SEComponent;
import simelectricity.energynet.components.SEComponent.Tile;
import simelectricity.energynet.components.Wire;

import java.util.*;

public class EnergyNetDataProvider extends SavedData {
    private static final String DATA_NAME = SimElectricity.MODID + "_GridData";
    //-----------------------------------------------------------------------------------------------------------
    ///////////////////////////////////////
    /// GridTile update notification
    ///////////////////////////////////////
    private final Set<ISEGridBlockEntity> updatedGridTile = new HashSet<>();
    //Map between coord and GridNode
    private final HashMap<BlockPos, GridNode> gridNodeMap = new HashMap<>();
    private final List<BlockEntity> loadedTiles = new LinkedList<>();
    //Records TE that associated with grid nodes
    private final List<BlockEntity> loadedGridTiles = new LinkedList<>();
    //Records the connection between components
    private final SEGraph tileEntityGraph = new SEGraph();

    public static EnergyNetDataProvider get(Level world) {
        if (world.isClientSide)
            throw new RuntimeException("Cannot create SavedData on client side!");

        DimensionDataStorage storage = ((ServerLevel)world).getDataStorage();
        EnergyNetDataProvider instance = storage.computeIfAbsent(
        		new net.minecraft.world.level.saveddata.SavedData.Factory<>(
        				EnergyNetDataProvider::new,
        				EnergyNetDataProvider::load,
        				null
        		),
        		EnergyNetDataProvider.DATA_NAME
        );

        return instance;
    }


    //Tile Event handling ----------------------------------------------------------------------------

    //Utils ------------------------------------------------------------------------------
    public static BlockEntity getTileEntityOnDirection(BlockEntity te, Direction direction) {
        return te.getLevel().getBlockEntity(te.getBlockPos().relative(direction));
    }

    public static BlockEntity getTileEntityOnDirection(BlockEntity te, Direction direction, Direction direction2) {
        return te.getLevel().getBlockEntity(te.getBlockPos().relative(direction).relative(direction2));
    }

    public void onNewResultAvailable() {
        this.tileEntityGraph.updateVoltageCache();

        //Execute Handlers
        Iterator<BlockEntity> iterator = this.loadedTiles.iterator();
        while (iterator.hasNext()) {
            BlockEntity te = iterator.next();
            if (te instanceof ISEEnergyNetUpdateHandler)
                ((ISEEnergyNetUpdateHandler) te).onEnergyNetUpdate();
        }
        iterator = this.loadedGridTiles.iterator();
        while (iterator.hasNext()) {
            BlockEntity te = iterator.next();
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

    public void addTile(BlockEntity te) {
        if (this.loadedTiles.contains(te)) {
            SELogger.logWarn(SELogger.energyNet, "Duplicated BlockEntity:" + te + ", this could be a bug!");
        } else {
            if (te instanceof ISECableBlockEntity) {
                ISECableBlockEntity cableTile = (ISECableBlockEntity) te;
                Cable cable = (Cable) cableTile.getNode();
                this.tileEntityGraph.addVertex(cable);
            } if (te instanceof ISEBlockEntity) {
                ISEBlockEntity tile = (ISEBlockEntity) te;

                for (Direction direction : Direction.values()) {
                    ISESubComponent<?> subComponent = tile.getComponent(direction);
                    if (subComponent != null) {
                        this.tileEntityGraph.addVertex((SEComponent) subComponent);
                    }
                }
            }
            this.loadedTiles.add(te);
        }
    }

    private static boolean isSideSolid(Level world, BlockPos pos, Direction facing) {
        return world.getBlockState(pos).isFaceSturdy(world, pos, facing);
    }

    public void updateTileConnection(BlockEntity te) {
    	if (!this.loadedTiles.contains(te))
            return;

        if (te instanceof ISECableBlockEntity) {
            ISECableBlockEntity cableTile = (ISECableBlockEntity) te;
            Cable cable = (Cable) cableTile.getNode();

            this.tileEntityGraph.isolateVertex(cable);

            if (cable.isGridInterConnectionPoint) {
                GridNode gridNode = getGridObjectAtCoord(te.getBlockPos());
                SEGraph.interconnection(cable, gridNode);
            }

            //Build connection with neighbors
            for (Direction direction : Direction.values()) {
                BlockEntity neighborTileEntity = getTileEntityOnDirection(te, direction);

                if (!cable.canConnectOnSide(direction))
                	continue;

                if (neighborTileEntity instanceof ISECableBlockEntity) {  //Conductor
                    ISECableBlockEntity neighborCableTile = (ISECableBlockEntity) neighborTileEntity;
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
                } else if (neighborTileEntity instanceof ISEBlockEntity) {
                    ISEBlockEntity tile = (ISEBlockEntity) neighborTileEntity;
                    ISESubComponent<?> component = tile.getComponent(direction.getOpposite());

                    if (component != null) {
                        this.tileEntityGraph.addEdge((SEComponent) component, cable);
                    }
                }
            }
        } else if (te instanceof ISEBlockEntity) {
            ISEBlockEntity tile = (ISEBlockEntity) te;

            for (Direction direction : Direction.values()) {
                ISESubComponent<?> subComponent = tile.getComponent(direction);
                if (subComponent != null) {
                    this.tileEntityGraph.isolateVertex((SEComponent) subComponent);
                }
            }

            if (te instanceof ISEWireBlockEntity) {
                ISEWireBlockEntity wireTile = (ISEWireBlockEntity) te;
                for (Direction side : Direction.values()) {
                    Wire wire = (Wire) wireTile.getComponent(side);

                    // Solve connections with cable and ISEBlockEntity
                    if (wire.hasBranchOnSide(null)) {
                        BlockEntity neighborTileEntity = getTileEntityOnDirection(te, side);
                        if (neighborTileEntity instanceof ISECableBlockEntity) {
                            Cable cable = (Cable) ((ISECableBlockEntity) neighborTileEntity).getNode();
                            // Connected properly
                            if (cable.canConnectOnSide(side.getOpposite()))
                                this.tileEntityGraph.addEdge(cable, wire);
                        } else if (neighborTileEntity instanceof ISEBlockEntity && !(neighborTileEntity instanceof ISEWireBlockEntity)) {
                            this.tileEntityGraph.addEdge((SEComponent) ((ISEBlockEntity) neighborTileEntity).getComponent(side.getOpposite()), wire);
                        }
                    }

                    for (Direction branch: Direction.values()) {
                        if (branch.getAxis() == side.getAxis())
                            continue;

                        if (!wire.hasBranchOnSide(branch))
                            continue;

                        Wire wireNeighbor = (Wire) wireTile.getComponent(branch);
                        // Connections within the ISEWireBlockEntity
                        if (wireNeighbor.hasBranchOnSide(side))
                            this.tileEntityGraph.addEdge(wire, wireNeighbor);

                        // Co-planar Connections
                        BlockEntity neighborTileEntity = getTileEntityOnDirection(te, branch);
                        if (neighborTileEntity instanceof ISEWireBlockEntity) {
                            wireNeighbor = (Wire) ((ISEWireBlockEntity) neighborTileEntity).getComponent(side);
                            if (wireNeighbor.hasBranchOnSide(branch.getOpposite()))
                                this.tileEntityGraph.addEdge(wire, wireNeighbor);
                        }

                        // Corner connections
                        neighborTileEntity = getTileEntityOnDirection(te, side, branch);
                        if (neighborTileEntity instanceof ISEWireBlockEntity) {
                            wireNeighbor = (Wire) ((ISEWireBlockEntity) neighborTileEntity).getComponent(branch.getOpposite());
                            if (wireNeighbor.hasBranchOnSide(side.getOpposite()) &&
                                    !isSideSolid(te.getLevel(), te.getBlockPos().relative(branch), side) &&
                                    !isSideSolid(te.getLevel(), te.getBlockPos().relative(branch), branch.getOpposite()))
                                this.tileEntityGraph.addEdge(wire, wireNeighbor);
                        }
                    }
                }
            } else {
                // ISEBlockEntity
                for (Direction direction : Direction.values()) {
                    ISESubComponent<?> subComponent = tile.getComponent(direction);
                    if (subComponent != null) {
                        BlockEntity neighborTileEntity = getTileEntityOnDirection(te, direction);

                        if (neighborTileEntity instanceof ISECableBlockEntity) {
                            Cable cable = (Cable) ((ISECableBlockEntity) neighborTileEntity).getNode();
                            // Connected properly
                            if (cable.canConnectOnSide(direction.getOpposite()))
                                this.tileEntityGraph.addEdge(cable, (SEComponent) subComponent);
                        } else if (neighborTileEntity instanceof ISEWireBlockEntity) {
                            Wire wire = (Wire) ((ISEWireBlockEntity) neighborTileEntity).getComponent(direction.getOpposite());
                            if (wire.hasBranchOnSide(null))
                                this.tileEntityGraph.addEdge(wire, (SEComponent) subComponent);
                        }
                    }
                }
            }
        } else {
            throw new RuntimeException("Unexpected BlockEntity:" + te);
        }
    }

    public void updateTileParam(BlockEntity te) {
    	if (!this.loadedTiles.contains(te))
            return;

        if (te instanceof ISECableBlockEntity) {
            Cable cable = (Cable) ((ISECableBlockEntity) te).getNode();
            cable.updateComponentParameters();
        } else if (te instanceof ISEBlockEntity) {
            ISEBlockEntity tile = (ISEBlockEntity) te;
            for (Direction direction : Direction.values()) {
                ISESubComponent<?> subComponent = tile.getComponent(direction);

                if (subComponent instanceof Tile)
                    ((Tile<?>) subComponent).updateComponentParameters();
            }
        } else {
            throw new RuntimeException("Unexpected BlockEntity:" + te);
        }
    }

    public void removeTile(BlockEntity te) {
    	if (!this.loadedTiles.contains(te))
            return;

        if (te instanceof ISECableBlockEntity) {
            Cable cable = (Cable) ((ISECableBlockEntity) te).getNode();

            this.tileEntityGraph.removeVertex(cable);
        } else if (te instanceof ISEBlockEntity) {
            ISEBlockEntity tile = (ISEBlockEntity) te;
            for (Direction direction : Direction.values()) {
                ISESubComponent<?> subComponent = tile.getComponent(direction);
                if (subComponent != null) {
                    this.tileEntityGraph.removeVertex((SEComponent) subComponent);
                }
            }
        } else {
            throw new RuntimeException("Unexpected BlockEntity:" + te);
        }

        this.loadedTiles.remove(te);
    }

    //Grid Event handling ----------------------------------------------------------------------------
    public void addGridNode(GridNode gridNode) {
        this.tileEntityGraph.addVertex(gridNode);
        this.gridNodeMap.put(gridNode.getPos(), gridNode);

        setDirty();
    }

    public void removeGridNode(GridNode gridNode) {
        if (gridNode == null)
            return;            //TODO should we log this?

        for (GridNode affectedNeighbors : this.tileEntityGraph.removeGridVertex(gridNode)) {
            BlockEntity te = affectedNeighbors.te;
            if (te instanceof ISEGridBlockEntity)
                this.updatedGridTile.add((ISEGridBlockEntity) te);
        }

        this.gridNodeMap.remove(gridNode.getPos());
        setDirty();
    }

    public void addGridConnection(GridNode node1, GridNode node2, double resistance) {
        if (node1 == null || node2 == null)
            return;

        this.tileEntityGraph.addGridEdge(node1, node2, resistance);

        BlockEntity te1 = node1.te;
        BlockEntity te2 = node2.te;

        if (te1 instanceof ISEGridBlockEntity)
            this.updatedGridTile.add((ISEGridBlockEntity) te1);
        if (te2 instanceof ISEGridBlockEntity)
            this.updatedGridTile.add((ISEGridBlockEntity) te2);

        setDirty();
    }

    public void removeGridConnection(GridNode node1, GridNode node2) {
        if (node1 == null || node2 == null)
            return;

        this.tileEntityGraph.removeGridEdge(node1, node2);

        BlockEntity te1 = node1.te;
        BlockEntity te2 = node2.te;

        if (te1 instanceof ISEGridBlockEntity)
            this.updatedGridTile.add((ISEGridBlockEntity) te1);
        if (te2 instanceof ISEGridBlockEntity)
            this.updatedGridTile.add((ISEGridBlockEntity) te2);

        setDirty();
    }


    //-----------------------------------------------------------------------------------------------------------
    ///////////////////////////////////////
    ///PerWorldStorage
    ///////////////////////////////////////

    public void makeTransformer(GridNode primary, GridNode secondary, double ratio, double resistance) {
        if (primary == null || secondary == null)
            return;

        this.tileEntityGraph.makeTransformer(primary, secondary, ratio, resistance);

        BlockEntity te1 = primary.te;
        BlockEntity te2 = secondary.te;

        if (te1 instanceof ISEGridBlockEntity)
            this.updatedGridTile.add((ISEGridBlockEntity) te1);
        if (te2 instanceof ISEGridBlockEntity)
            this.updatedGridTile.add((ISEGridBlockEntity) te2);

        setDirty();
    }

    public void breakTransformer(GridNode node) {
        if (node == null)
            return;

        this.tileEntityGraph.breakTransformer(node);

        BlockEntity te = node.te;

        if (te instanceof ISEGridBlockEntity)
            this.updatedGridTile.add((ISEGridBlockEntity) te);

        setDirty();
    }

    public void onGridTilePresent(BlockEntity te) {
        ISEGridBlockEntity gridTile = (ISEGridBlockEntity) te;
        GridNode gridObject = this.gridNodeMap.get(te.getBlockPos());
        if (gridObject == null)
            return;
        this.loadedGridTiles.add(te);
        gridObject.te = te;
        gridTile.setGridNode(gridObject);
        this.updatedGridTile.add(gridTile);
    }

    public void onGridTileInvalidate(BlockEntity te) {
    	if (!this.loadedTiles.contains(te))
            return;

        this.loadedGridTiles.remove(te);

        GridNode gridObject = this.gridNodeMap.get(te.getBlockPos());

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
    public static EnergyNetDataProvider load(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider registries) {
    	EnergyNetDataProvider ret = new EnergyNetDataProvider();
    	ret.gridNodeMap.clear();

        ListTag NBTObjects = nbt.getList("Objects", Tag.TAG_COMPOUND);
        for (int i = 0; i < NBTObjects.size(); i++) {
            CompoundTag compound = NBTObjects.getCompound(i);
            GridNode obj = new GridNode(compound);
            //byte type = compound.getByte("type");


            //throw new RuntimeException("Undefined grid object type");

            //obj.readFromNBT(compound);
            ret.tileEntityGraph.addVertex(obj);

            ret.gridNodeMap.put(obj.getPos(), obj);
        }


        //Build node connections
        for (GridNode gridNode : ret.gridNodeMap.values()) {
            gridNode.buildNeighborConnection(ret.gridNodeMap, ret.tileEntityGraph);
        }

        SELogger.logInfo(SELogger.energyNet, "Loaded GridNodes from world NBT storage");

        return ret;
    }

    @Override
    public CompoundTag save(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider registries) {
        ListTag NBTNodes = new ListTag();
        for (GridNode gridNode : this.gridNodeMap.values()) {
            CompoundTag tag = new CompoundTag();
            gridNode.writeToNBT(tag);
            NBTNodes.add(tag);
        }
        nbt.put("Objects", NBTNodes);

        return nbt;
    }

    public void fireGridTileUpdateEvent() {
        for (ISEGridBlockEntity gridTile : this.updatedGridTile) {
            gridTile.onGridNeighborUpdated();
        }
        this.updatedGridTile.clear();
    }
}
