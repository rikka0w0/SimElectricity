package simelectricity.essential.cable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEWire;
import simelectricity.api.node.ISESubComponent;
import simelectricity.api.tile.ISEWireTile;
import simelectricity.essential.api.ISEGenericWire;
import simelectricity.essential.common.SEEnergyTile;

import java.util.List;

public class TileWire extends SEEnergyTile implements ISEGenericWire {
    private final Wire[] wires;
    private final ISESubComponent[] nodes;

    public static class Wire implements ISEWire {
        public final TileWire parent;
        public final EnumFacing side;
        private final boolean[] connections;

        public double resistance;
        public ItemStack itemStack;

        public Wire(TileWire parent, EnumFacing side) {
            this.parent = parent;
            this.side = side;
            this.connections = new boolean[EnumFacing.values().length];

            this.itemStack = ItemStack.EMPTY;
        }

        @Override
        public boolean hasBranchOnSide(EnumFacing side) {
            if (side == null){
                for (EnumFacing dir: EnumFacing.VALUES)
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

        public void setConnection(EnumFacing branch, boolean connection) {
            this.connections[branch.ordinal()] = connection;
        }

        public ItemStack getItemToDrop() {
            ItemStack stack = this.itemStack.copy();
            stack.setCount(1);
            return stack;
        }

        public ItemStack getItemToDropAll() {
            int count = 0;
            for (EnumFacing branch: EnumFacing.VALUES)
                if (connections[branch.ordinal()])
                    count++;

            ItemStack stack = this.itemStack.copy();
            stack.setCount(count);
            return stack;
        }

        private void readFromNBT(NBTTagCompound tagCompound) {
            byte connection_dat = tagCompound.getByte("connections");
            for (EnumFacing side: EnumFacing.VALUES) {
                this.connections[side.ordinal()] = (connection_dat & (1<<side.ordinal())) > 0;
            }

            if (tagCompound.hasKey("resistance"))
                this.resistance = tagCompound.getDouble("resistance");
            else
                this.resistance = 0.1;

            this.itemStack = new ItemStack(tagCompound.getCompoundTag("itemStack"));
        }

        private void writeToNBT(NBTTagCompound compound) {
            byte connection_dat = 0;
            for (EnumFacing side: EnumFacing.VALUES) {
                if (this.hasBranchOnSide(side))
                    connection_dat |= (1 << side.ordinal());
            }
            compound.setByte("connections", connection_dat);


            compound.setDouble("resistance", this.resistance);
            NBTTagCompound itemStackCompound = new NBTTagCompound();
            this.itemStack.writeToNBT(itemStackCompound);
            compound.setTag("itemStack", itemStackCompound);
        }
    }

    public TileWire() {
        this.nodes = new ISESubComponent[EnumFacing.VALUES.length];

        this.wires = new Wire[EnumFacing.VALUES.length];
        for (EnumFacing side : EnumFacing.VALUES)
            this.wires[side.ordinal()] = new Wire(this, side);
    }

    ///////////////////////////////////
    /// TileEntity
    ///////////////////////////////////

    @Override
    public void onLoad() {
        for (EnumFacing side : EnumFacing.VALUES)
            this.nodes[side.ordinal()] = SEAPI.energyNetAgent.newComponent(this.wires[side.ordinal()], this);

        super.onLoad();
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        for (EnumFacing side : EnumFacing.values())
            wires[side.ordinal()].readFromNBT(tagCompound.getCompoundTag(side.getName()));

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        for (EnumFacing side : EnumFacing.values()) {
            NBTTagCompound nbt = new NBTTagCompound();
            wires[side.ordinal()].writeToNBT(nbt);
            tagCompound.setTag(side.getName(), nbt);
        }

        return super.writeToNBT(tagCompound);
    }

    ///////////////////////////////////
    /// ISEWireTile
    ///////////////////////////////////
    @Override
    public ISESubComponent getComponent(EnumFacing side) {
        return this.nodes[side.ordinal()];
    }

    @Override
    public ISEWire getWireParam(EnumFacing side) {
        return this.wires[side.ordinal()];
    }

    ///////////////////////////////////
    /// ISEChunkWatchSensitiveTile
    ///////////////////////////////////
    @Override
    public void onRenderingUpdateRequested() {
        for (EnumFacing wire_side: EnumFacing.VALUES) {
            for (EnumFacing branch: EnumFacing.VALUES) {
                int index = BlockWire.cornerIdOf(wire_side, branch);
                if (index < 0)
                    continue;

                if (!this.getWireParam(wire_side).hasBranchOnSide(branch)) {
                    externalConnections[index] = false;
                    continue;
                }

                TileEntity neighbor = world.getTileEntity(pos.offset(wire_side).offset(branch));

                if (neighbor instanceof ISEWireTile) {
                    ISEWireTile wireTileNeighbor = (ISEWireTile) neighbor;

                    externalConnections[index] =
                                    wireTileNeighbor.getWireParam(branch.getOpposite()).hasBranchOnSide(wire_side.getOpposite()) &&
                                    !world.isSideSolid(pos.offset(branch), branch.getOpposite()) &&
                                    !world.isSideSolid(pos.offset(branch), wire_side);
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
    private boolean[] connectedOnSide = new boolean[EnumFacing.VALUES.length];

    @Override
    public void prepareS2CPacketData(NBTTagCompound tagCompound) {
        super.prepareS2CPacketData(tagCompound);

        byte connections = 0;
        for (EnumFacing side : EnumFacing.values()) {
            NBTTagCompound nbt = new NBTTagCompound();
            wires[side.ordinal()].writeToNBT(nbt);
            tagCompound.setTag(side.getName(), nbt);

            if (connectedOnSide[side.ordinal()])
                connections |= (1 << side.ordinal());
        }
        tagCompound.setByte("connections", connections);

        int extcon = 0;
        for (int i=0; i<externalConnections.length; i++) {
            if (externalConnections[i])
                extcon |= 1<<i;
        }
        tagCompound.setInteger("extcon", extcon);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onSyncDataFromServerArrived(NBTTagCompound tagCompound) {
        byte connections = tagCompound.getByte("connections");
        for (EnumFacing side : EnumFacing.VALUES) {
            wires[side.ordinal()].readFromNBT(tagCompound.getCompoundTag(side.getName()));
            connectedOnSide[side.ordinal()] = (connections & (1 << side.ordinal())) > 0;
        }

        int extcon = tagCompound.getInteger("extcon");
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
    public boolean hasExtConnection(EnumFacing f1, EnumFacing f2) {
        int index = BlockWire.cornerIdOf(f1, f2);
        return index<0 ? false : externalConnections[index];
    }

    @Override
    public boolean connectedOnSide(EnumFacing side) {
        return connectedOnSide[side.ordinal()];
    }

    @Override
    public int getWireType(EnumFacing side) {
        return this.wires[side.ordinal()].itemStack.getItemDamage();
    }

    @Override
    public boolean canAddBranch(EnumFacing side, EnumFacing to, ItemStack itemStack) {
        if (this.wires[side.ordinal()].hasBranchOnSide(null)) {
            // Already have some branches
            return itemStack.isItemEqual(this.wires[side.ordinal()].itemStack);
        } else {
            return true;
        }
    }

    @Override
    public void addBranch(EnumFacing side, EnumFacing to, ItemStack itemStack, double resistance) {
        if (!this.wires[side.ordinal()].hasBranchOnSide(null)) {
            // No branch exist
            this.wires[side.ordinal()].itemStack = itemStack;
            this.wires[side.ordinal()].resistance = resistance;
        }
        this.wires[side.ordinal()].setConnection(to, true);

        world.neighborChanged(pos.offset(side), getBlockType(), pos);
        notifyExtCornerOfStateChange(side, to);

        updateTileConnection();

        onRenderingUpdateRequested();
    }

    void notifyExtCornerOfStateChange(EnumFacing side, EnumFacing to) {
        TileEntity potentialNeighbor = world.getTileEntity(pos.offset(side).offset(to));
        if (    potentialNeighbor instanceof ISEGenericWire &&
                !world.isSideSolid(pos.offset(to), to.getOpposite()) &&
                !world.isSideSolid(pos.offset(to), side.getOpposite())) {
            ISEGenericWire wireTile = (ISEGenericWire)potentialNeighbor;
            if (wireTile.getWireParam(to.getOpposite()).hasBranchOnSide(side.getOpposite()))
                wireTile.onRenderingUpdateRequested();
        }
    }

    @Override
    public void removeBranch(EnumFacing side, EnumFacing to, List<ItemStack> drops) {
        if (to == null) {
            drops.add(this.wires[side.ordinal()].getItemToDropAll());
            for (EnumFacing facing: EnumFacing.VALUES) {
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

        world.neighborChanged(pos.offset(side), getBlockType(), pos);

        updateTileConnection();

        onRenderingUpdateRequested();
    }
}
