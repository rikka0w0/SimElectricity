package simelectricity.essential.common.semachine;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.ISidedFacing;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEComponentParameter;
import simelectricity.api.node.ISESubComponent;
import simelectricity.api.tile.ISETile;
import simelectricity.essential.common.SEEnergyTile;
import simelectricity.essential.utils.Utils;

public class SETwoPortMachine extends SEEnergyTile implements ISidedFacing, ISETile, ISEComponentParameter {
    public EnumFacing inputSide = EnumFacing.SOUTH;
    public EnumFacing outputSide = EnumFacing.NORTH;
    protected EnumFacing facing = EnumFacing.NORTH;
    protected ISESubComponent input = SEAPI.energyNetAgent.newComponent(this, this);

    ///////////////////////////////////
    /// TileEntity
    ///////////////////////////////////
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        this.inputSide = Utils.facingFromNbt(tagCompound, "inputSide");
        this.outputSide = Utils.facingFromNbt(tagCompound, "outputSide");
        this.facing = Utils.facingFromNbt(tagCompound, "facing");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setByte("inputSide", (byte) this.inputSide.ordinal());
        tagCompound.setByte("outputSide", (byte) this.outputSide.ordinal());
        tagCompound.setByte("facing", (byte) this.facing.ordinal());

        return super.writeToNBT(tagCompound);
    }

    @Override
    public EnumFacing getFacing() {
        return this.facing;
    }

    ///////////////////////////////////
    /// ISidedFacing
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

    /////////////////////////////////////////////////////////
    ///Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(NBTTagCompound nbt) {
        super.prepareS2CPacketData(nbt);

        nbt.setByte("inputSide", (byte) this.inputSide.ordinal());
        nbt.setByte("outputSide", (byte) this.outputSide.ordinal());
        nbt.setByte("facing", (byte) this.facing.ordinal());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
        this.inputSide = EnumFacing.getFront(nbt.getByte("inputSide"));
        this.outputSide = EnumFacing.getFront(nbt.getByte("outputSide"));
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
        if (side == this.inputSide)
            return this.input;
        else if (side == this.outputSide)
            return this.input.getComplement();
        else
            return null;
    }

    /////////////////////////////////////////////////////////
    /// Utils
    /////////////////////////////////////////////////////////
    public void setFunctionalSide(EnumFacing input, EnumFacing output) {
        inputSide = input;
        outputSide = output;

        markTileEntityForS2CSync();
        this.world.notifyNeighborsOfStateChange(this.pos, getBlockType(), true);
        //this.worldObj.notifyBlockChange(xCoord, yCoord, zCoord, this.getBlockType());

        if (isAddedToEnergyNet)
            SEAPI.energyNetAgent.updateTileConnection(this);
    }
}
