package simelectricity.essential.cable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelDataMap;
import rikka.librikka.block.BlockUtils;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEWire;
import simelectricity.api.node.ISESubComponent;
import simelectricity.api.tile.ISEWireTile;
import simelectricity.essential.api.ISEGenericWire;
import simelectricity.essential.common.SEEnergyTile;

import java.util.List;

public class TileWire extends SEEnergyTile implements ISEGenericWire {
    private final Wire[] wires;
    private final ISESubComponent<?>[] nodes;

    public static class Wire implements ISEWire {
        public final TileWire parent;
        public final Direction side;
        private final boolean[] connections;

        public double resistance;
        public ItemStack itemStack;

        public Wire(TileWire parent, Direction side) {
            this.parent = parent;
            this.side = side;
            this.connections = new boolean[Direction.values().length];

            this.itemStack = ItemStack.EMPTY;
        }

        @Override
        public boolean hasBranchOnSide(Direction side) {
            if (side == null){
                for (Direction dir: Direction.values())
                    if (this.connections[dir.ordinal()])
                        return true;

                return false;
            }

            return this.connections[side.ordinal()];
        }

        @Override
        public double getResistance() {
            return resistance;
        }

        @Override
        public boolean hasShuntResistance() {
            return false;
        }

        @Override
        public double getShuntResistance() {
            return 0;
        }

        public void setConnection(Direction branch, boolean connection) {
            this.connections[branch.ordinal()] = connection;
        }

        public ItemStack getItemToDrop() {
            ItemStack stack = this.itemStack.copy();
            stack.setCount(1);
            return stack;
        }

        public ItemStack getItemToDropAll() {
            int count = 0;
            for (Direction branch: Direction.values())
                if (connections[branch.ordinal()])
                    count++;

            ItemStack stack = this.itemStack.copy();
            stack.setCount(count);
            return stack;
        }

        private void readFromNBT(CompoundTag tagCompound) {
            byte connection_dat = tagCompound.getByte("connections");
            for (Direction side: Direction.values()) {
                this.connections[side.ordinal()] = (connection_dat & (1<<side.ordinal())) > 0;
            }

            if (tagCompound.contains("resistance"))
                this.resistance = tagCompound.getDouble("resistance");
            else
                this.resistance = 0.1;

            this.itemStack = ItemStack.of(tagCompound.getCompound("itemStack"));
        }

        private void write(CompoundTag compound) {
            byte connection_dat = 0;
            for (Direction side: Direction.values()) {
                if (this.hasBranchOnSide(side))
                    connection_dat |= (1 << side.ordinal());
            }
            compound.putByte("connections", connection_dat);


            compound.putDouble("resistance", this.resistance);
            CompoundTag itemStackCompound = new CompoundTag();
            this.itemStack.save(itemStackCompound);
            compound.put("itemStack", itemStackCompound);
        }
    }

    public TileWire(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
    	super(beType, pos, blockState);
        this.nodes = new ISESubComponent[Direction.values().length];

        this.wires = new Wire[Direction.values().length];
        for (Direction side : Direction.values())
            this.wires[side.ordinal()] = new Wire(this, side);
    }

    ///////////////////////////////////
    /// BlockEntity
    ///////////////////////////////////

    @Override
    public void clearRemoved() {
        for (Direction side : Direction.values())
            this.nodes[side.ordinal()] = SEAPI.energyNetAgent.newComponent(this.wires[side.ordinal()], this);

        super.clearRemoved();
    }

    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);

        for (Direction side : Direction.values())
            wires[side.ordinal()].readFromNBT(tagCompound.getCompound(side.getSerializedName()));
    }

    @Override
    public CompoundTag save(CompoundTag tagCompound) {
        for (Direction side : Direction.values()) {
            CompoundTag nbt = new CompoundTag();
            wires[side.ordinal()].write(nbt);
            tagCompound.put(side.getSerializedName(), nbt);
        }

        return super.save(tagCompound);
    }

    ///////////////////////////////////
    /// ISEWireTile
    ///////////////////////////////////
    @Override
    public ISESubComponent<?> getComponent(Direction side) {
        return this.nodes[side.ordinal()];
    }

    @Override
    public ISEWire getWireParam(Direction side) {
        return this.wires[side.ordinal()];
    }

    ///////////////////////////////////
    /// ISEChunkWatchSensitiveTile
    ///////////////////////////////////
    @Override
    public void onRenderingUpdateRequested() {
        for (Direction wire_side: Direction.values()) {
            for (Direction branch: Direction.values()) {
                int index = BlockWire.cornerIdOf(wire_side, branch);
                if (index < 0)
                    continue;

                if (!this.getWireParam(wire_side).hasBranchOnSide(branch)) {
                    externalConnections[index] = false;
                    continue;
                }

                BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(wire_side).relative(branch));

                if (neighbor instanceof ISEWireTile) {
                    ISEWireTile wireTileNeighbor = (ISEWireTile) neighbor;

                    externalConnections[index] =
                                    wireTileNeighbor.getWireParam(branch.getOpposite()).hasBranchOnSide(wire_side.getOpposite()) &&
                                    !BlockUtils.isSideSolid(level, worldPosition.relative(branch), branch.getOpposite()) &&
                                    !BlockUtils.isSideSolid(level, worldPosition.relative(branch), wire_side);
                } else {
                    externalConnections[index] = false;
                }
            }

            connectedOnSide[wire_side.ordinal()] = SEAPI.energyNetAgent.canConnectTo(this, wire_side);
        }

        //Initiate Server->Client synchronization
        this.markTileEntityForS2CSync();
    }

    ////////////////////////////////////////
    /// Server->Client sync
    ////////////////////////////////////////
    private boolean[] externalConnections = new boolean[BlockWire.corners.length];
    private boolean[] connectedOnSide = new boolean[Direction.values().length];

    @Override
    public void prepareS2CPacketData(CompoundTag tagCompound) {
        super.prepareS2CPacketData(tagCompound);

        byte connections = 0;
        for (Direction side : Direction.values()) {
            CompoundTag nbt = new CompoundTag();
            wires[side.ordinal()].write(nbt);
            tagCompound.put(side.getSerializedName(), nbt);

            if (connectedOnSide[side.ordinal()])
                connections |= (1 << side.ordinal());
        }
        tagCompound.putByte("connections", connections);

        int extcon = 0;
        for (int i=0; i<externalConnections.length; i++) {
            if (externalConnections[i])
                extcon |= 1<<i;
        }
        tagCompound.putInt("extcon", extcon);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onSyncDataFromServerArrived(CompoundTag tagCompound) {
        byte connections = tagCompound.getByte("connections");
        for (Direction side : Direction.values()) {
            wires[side.ordinal()].readFromNBT(tagCompound.getCompound(side.getSerializedName()));
            connectedOnSide[side.ordinal()] = (connections & (1 << side.ordinal())) > 0;
        }

        int extcon = tagCompound.getInt("extcon");
        for (int i=0; i<externalConnections.length; i++)
            externalConnections[i] = (extcon & (1<<i)) > 0;

        super.onSyncDataFromServerArrived(tagCompound);

        // Flag 1 - update Rendering Only!
        this.markForRenderUpdate();
    }

    ////////////////////////////////////////
    /// ISEGenericWire
    ////////////////////////////////////////
    @Override
    public boolean hasExtConnection(Direction f1, Direction f2) {
        int index = BlockWire.cornerIdOf(f1, f2);
        return index<0 ? false : externalConnections[index];
    }

    @Override
    public boolean connectedOnSide(Direction side) {
        return connectedOnSide[side.ordinal()];
    }

    @Override
    public boolean canAddBranch(Direction side, Direction to, ItemStack itemStack) {
        if (this.wires[side.ordinal()].hasBranchOnSide(null)) {
            // Already have some branches
            return itemStack.sameItem(this.wires[side.ordinal()].itemStack);
        } else {
            return true;
        }
    }

    @Override
    public void addBranch(Direction side, Direction to, ItemStack itemStack, double resistance) {
        if (!this.wires[side.ordinal()].hasBranchOnSide(null)) {
            // No branch exist
            this.wires[side.ordinal()].itemStack = itemStack;
            this.wires[side.ordinal()].resistance = resistance;
        }
        this.wires[side.ordinal()].setConnection(to, true);

        level.neighborChanged(worldPosition.relative(side), getBlockState().getBlock(), worldPosition);
        notifyExtCornerOfStateChange(side, to);

        updateTileConnection();

        onRenderingUpdateRequested();
    }

    void notifyExtCornerOfStateChange(Direction side, Direction to) {
        BlockEntity potentialNeighbor = level.getBlockEntity(worldPosition.relative(side).relative(to));
        if (    potentialNeighbor instanceof ISEGenericWire &&
                !BlockUtils.isSideSolid(level, worldPosition.relative(to), to.getOpposite()) &&
                !BlockUtils.isSideSolid(level, worldPosition.relative(to), side.getOpposite())) {
            ISEGenericWire wireTile = (ISEGenericWire)potentialNeighbor;
            if (wireTile.getWireParam(to.getOpposite()).hasBranchOnSide(side.getOpposite()))
                wireTile.onRenderingUpdateRequested();
        }
    }

    @Override
    public void removeBranch(Direction side, Direction to, List<ItemStack> drops) {
        if (to == null) {
            drops.add(this.wires[side.ordinal()].getItemToDropAll());
            for (Direction facing: Direction.values()) {
                if (this.wires[side.ordinal()].hasBranchOnSide(facing)) {
                    this.wires[side.ordinal()].setConnection(facing, false);
                    notifyExtCornerOfStateChange(side, facing);
                }
            }
        } else {
            drops.add(this.wires[side.ordinal()].getItemToDrop());
            this.wires[side.ordinal()].setConnection(to, false);
            notifyExtCornerOfStateChange(side, to);
        }

        level.neighborChanged(worldPosition.relative(side), getBlockState().getBlock(), worldPosition);

        updateTileConnection();

        onRenderingUpdateRequested();
    }

    @Override
    public ItemStack getItemDrop(Direction side) {
        return this.wires[side.ordinal()].getItemToDropAll();
    }

    protected void collectModelData(ModelDataMap.Builder builder) {
    	builder.withInitial(ISEGenericWire.prop, this);
    }
}
