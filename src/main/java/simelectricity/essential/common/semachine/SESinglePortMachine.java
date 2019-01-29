package simelectricity.essential.common.semachine;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.Utils;
import simelectricity.api.ISEWrenchable;
import simelectricity.api.ISESidedFacing;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEComponentParameter;
import simelectricity.api.node.ISESubComponent;
import simelectricity.api.tile.ISETile;
import simelectricity.essential.common.SEEnergyTile;

public abstract class SESinglePortMachine<T extends ISEComponentParameter> extends SEEnergyTile implements ISESidedFacing, ISEWrenchable, ISETile, ISEComponentParameter {
    protected EnumFacing functionalSide = EnumFacing.SOUTH;
    protected EnumFacing facing = EnumFacing.NORTH;
    protected final ISESubComponent circuit = SEAPI.energyNetAgent.newComponent(this, this);
    protected final T cachedParam = (T) circuit;

    ///////////////////////////////////
    /// TileEntity
    ///////////////////////////////////
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        this.functionalSide = Utils.facingFromNbt(tagCompound, "functionalSide");
        this.facing = Utils.facingFromNbt(tagCompound, "facing");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setByte("functionalSide", (byte) this.functionalSide.ordinal());
        tagCompound.setByte("facing", (byte) this.facing.ordinal());
        return super.writeToNBT(tagCompound);
    }

    @Override
    public EnumFacing getFacing() {
        return this.facing;
    }

    ///////////////////////////////////
    /// ISESidedFacing
    ///////////////////////////////////
    @Override
    public void setFacing(EnumFacing newFacing) {
        this.facing = newFacing;

        markTileEntityForS2CSync();
    }

    @Override
    public boolean canSetFacing(EnumFacing newFacing) {
        return true;
    }


    ///////////////////////////////////
    /// ISEWrenchable
    ///////////////////////////////////
    @Override
    public void onWrenchAction(EnumFacing side, boolean isCreativePlayer) {
        this.SetFunctionalSide(side);
    }

    @Override
    public boolean canWrenchBeUsed(EnumFacing side) {
        return true;
    }

    /////////////////////////////////////////////////////////
    ///Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(NBTTagCompound nbt) {
        super.prepareS2CPacketData(nbt);

        nbt.setByte("functionalSide", (byte) this.functionalSide.ordinal());
        nbt.setByte("facing", (byte) this.facing.ordinal());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
        this.functionalSide = EnumFacing.getFront(nbt.getByte("functionalSide"));
        this.facing = EnumFacing.getFront(nbt.getByte("facing"));

        // Flag 1 - update Rendering Only!
        this.markForRenderUpdate();

        super.onSyncDataFromServerArrived(nbt);
    }

    /////////////////////////////////////////////////////////
    ///ISETile
    /////////////////////////////////////////////////////////
    @Override
    public ISESubComponent getComponent(EnumFacing side) {
        return side == this.functionalSide ? this.circuit : null;
    }

    /////////////////////////////////////////////////////////
    ///Utils
    /////////////////////////////////////////////////////////
    public void SetFunctionalSide(EnumFacing side) {
        this.functionalSide = side;

        markTileEntityForS2CSync();
        this.world.notifyNeighborsOfStateChange(this.pos, getBlockType(), true);

        if (isAddedToEnergyNet)
            SEAPI.energyNetAgent.updateTileConnection(this);
    }
}
