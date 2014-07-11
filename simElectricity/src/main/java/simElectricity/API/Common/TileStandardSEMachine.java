package simElectricity.API.Common;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Energy;
import simElectricity.API.EnergyTile.IEnergyTile;
import simElectricity.API.ISidedFacing;
import simElectricity.API.Util;

/**
 * A standard SE machine can inherits this class, make things easier and less confusion
 */
public abstract class TileStandardSEMachine extends TileSidedFacingMachine implements IEnergyTile, ISidedFacing {
    protected ForgeDirection functionalSide = ForgeDirection.NORTH;

    protected boolean isAddedToEnergyNet = false;

    public void onLoad() {
    }

    public void onUnload() {
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!worldObj.isRemote && !isAddedToEnergyNet) {
            onLoad();
            Energy.postTileAttachEvent(this);
            this.isAddedToEnergyNet = true;
            Util.scheduleBlockUpdate(this);
        }
    }

    @Override
    public void invalidate() {
        if (!worldObj.isRemote & isAddedToEnergyNet) {
            onUnload();
            Energy.postTileDetachEvent(this);
            this.isAddedToEnergyNet = false;
        }

        super.invalidate();
    }


    //IEnergyTile
    @Override
    public ForgeDirection getFunctionalSide() {
        return functionalSide;
    }

    @Override
    public void setFunctionalSide(ForgeDirection newFunctionalSide) {
        functionalSide = newFunctionalSide;
    }

    @Override
    public boolean canSetFunctionalSide(ForgeDirection newFunctionalSide) {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        functionalSide = Util.byte2Direction(tagCompound.getByte("functionalSide"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setByte("functionalSide", Util.direction2Byte(functionalSide));
    }
}
