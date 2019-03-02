package simelectricity.essential.cable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEWire;
import simelectricity.api.node.ISESubComponent;
import simelectricity.essential.BlockRegistry;
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

        public Wire(TileWire parent, EnumFacing side) {
            this.parent = parent;
            this.side = side;
            this.connections = new boolean[EnumFacing.values().length];
        }

        @Override
        public boolean hasBranchOnSide(EnumFacing side) {
            return this.connections[side.ordinal()];
        }

        @Override
        public double getResistance() {
            return 0.1;
        }

        @Override
        public boolean hasShuntResistance() {
            return false;
        }

        @Override
        public double getShuntResistance() {
            return 0;
        }

        @SideOnly(Side.CLIENT)
        public void setConnection(EnumFacing branch, boolean connection) {
            this.connections[branch.ordinal()] = connection;
        }

        private void readFromNBT(NBTTagCompound tagCompound) {
            byte connection_dat = tagCompound.getByte("connections");
            for (EnumFacing side: EnumFacing.VALUES) {
                this.connections[side.ordinal()] = (connection_dat & (1<<side.ordinal())) > 0;
            }
        }

        private void writeToNBT(NBTTagCompound compound) {
            byte connection_dat = 0;
            for (EnumFacing side: EnumFacing.VALUES) {
                if (this.hasBranchOnSide(side))
                    connection_dat |= (1 << side.ordinal());
            }
            compound.setByte("connections", connection_dat);
        }
    }

    public TileWire() {
        this.nodes = new ISESubComponent[EnumFacing.VALUES.length];

        this.wires = new Wire[EnumFacing.VALUES.length];
        for (EnumFacing side : EnumFacing.VALUES)
            this.wires[side.ordinal()] = new Wire(this, side);
    }

    @Override
    public void onLoad() {
        for (EnumFacing side : EnumFacing.VALUES)
            this.nodes[side.ordinal()] = SEAPI.energyNetAgent.newComponent(this.wires[side.ordinal()], this);

        super.onLoad();
    }

    @Override
    public ISESubComponent getWireOnSide(EnumFacing side) {
        return this.nodes[side.ordinal()];
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

    @Override
    public void onRenderingUpdateRequested() {
        //Initiate Server->Client synchronization
        this.markTileEntityForS2CSync();
    }

    ////////////////////////////////////////
    //Server->Client sync
    ////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(NBTTagCompound tagCompound) {
        super.prepareS2CPacketData(tagCompound);

        for (EnumFacing side : EnumFacing.values()) {
            NBTTagCompound nbt = new NBTTagCompound();
            wires[side.ordinal()].writeToNBT(nbt);
            tagCompound.setTag(side.getName(), nbt);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onSyncDataFromServerArrived(NBTTagCompound tagCompound) {
        for (EnumFacing side : EnumFacing.values())
            wires[side.ordinal()].readFromNBT(tagCompound.getCompoundTag(side.getName()));

        super.onSyncDataFromServerArrived(tagCompound);

        // Flag 1 - update Rendering Only!
        this.markForRenderUpdate();
    }

    // @SideOnly(Side.CLIENT)
    @Override
    public boolean hasBranch(EnumFacing side, EnumFacing to) {
        if (to == null){
            for (EnumFacing dir: EnumFacing.VALUES)
                if (this.wires[side.ordinal()].hasBranchOnSide(dir))
                    return true;

            return false;
        }

        return this.wires[side.ordinal()].hasBranchOnSide(to);
    }

    @Override
    public void addBranch(EnumFacing side, EnumFacing to, ItemStack itemStack) {
        this.wires[side.ordinal()].setConnection(to, true);

        updateTileConnection();

        // Flag 1 - update Rendering Only!
        this.markForRenderUpdate();
    }

    @Override
    public void removeBranch(EnumFacing side, EnumFacing to, List<ItemStack> drops) {
        if (to == null) {
            for (EnumFacing facing: EnumFacing.VALUES) {
                if (this.wires[side.ordinal()].hasBranchOnSide(facing)) {
                    drops.add(new ItemStack(BlockRegistry.blockWire.itemBlock));
                    this.wires[side.ordinal()].setConnection(facing, false);
                }
            }
        } else {
            this.wires[side.ordinal()].setConnection(to, false);
            drops.add(new ItemStack(BlockRegistry.blockWire.itemBlock));
        }

        updateTileConnection();

        // Flag 1 - update Rendering Only!
        this.markForRenderUpdate();
    }
}
