package simElectricity.Common.Blocks.TileEntity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.*;
import simElectricity.API.EnergyTile.IBaseComponent;
import simElectricity.API.EnergyTile.IConductor;
import simElectricity.API.EnergyTile.IConnectable;
import simElectricity.API.EnergyTile.IManualJunction;

import java.util.List;

public class TileSwitch extends TileEntity implements IManualJunction, IConnectable, ISyncPacketHandler, ISidedFacing, IUpdateOnWatch, IEnergyNetUpdateHandler {
    protected boolean isAddedToEnergyNet = false;

    public ForgeDirection inputSide = ForgeDirection.NORTH, outputSide = ForgeDirection.SOUTH, facing = ForgeDirection.WEST;
    public float resistance = 0.1F;
    public float maxCurrent = 1F;
    public boolean isOn = false;

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!worldObj.isRemote && !isAddedToEnergyNet) {
            Energy.postTileAttachEvent(this);
            this.isAddedToEnergyNet = true;
            Util.scheduleBlockUpdate(this);
        }
    }

    @Override
    public void invalidate() {
        if (!worldObj.isRemote & isAddedToEnergyNet) {
            Energy.postTileDetachEvent(this);
            this.isAddedToEnergyNet = false;
        }

        super.invalidate();
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        resistance = tagCompound.getFloat("resistance");
        maxCurrent = tagCompound.getFloat("maxCurrent");
        isOn = tagCompound.getBoolean("isOn");
        inputSide = Util.byte2Direction(tagCompound.getByte("inputSide"));
        outputSide = Util.byte2Direction(tagCompound.getByte("outputSide"));
        facing = Util.byte2Direction(tagCompound.getByte("facing"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setFloat("resistance", resistance);
        tagCompound.setFloat("maxCurrent", maxCurrent);
        tagCompound.setBoolean("isOn", isOn);
        tagCompound.setByte("inputSide", Util.direction2Byte(inputSide));
        tagCompound.setByte("outputSide", Util.direction2Byte(outputSide));
        tagCompound.setByte("facing", Util.direction2Byte(facing));
    }

    @Override
    public void onClient2ServerUpdate(String field, Object value, short type) {
        if (field.contains("inputSide") || field.contains("outputSide") || field.contains("isOn")) {
            Energy.postTileRejoinEvent(this);
            Util.scheduleBlockUpdate(this);
        } else if (field.contains("resistance")) {
            Energy.postTileChangeEvent(this);
        } else if (field.contains("maxCurrent")) {
            onEnergyNetUpdate();
        }
    }

    @Override
    public void onServer2ClientUpdate(String field, Object value, short type) {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public float getResistance() {
        return resistance;
    }

    @Override
    public boolean canConnectOnSide(ForgeDirection side) {
        return side == inputSide || side == outputSide;
    }

    @Override
    public void addNeighbors(List<IBaseComponent> list) {
        if (isOn) {
            TileEntity neighbor = Util.getTileEntityonDirection(this, inputSide);

            if (neighbor instanceof IConductor)
                list.add((IConductor) neighbor);

            neighbor = Util.getTileEntityonDirection(this, outputSide);

            if (neighbor instanceof IConductor)
                list.add((IConductor) neighbor);
        }
    }

    @Override
    public ForgeDirection getFacing() {
        return facing;
    }

    @Override
    public void setFacing(ForgeDirection newFacing) {
        facing = newFacing;
    }

    @Override
    public boolean canSetFacing(ForgeDirection newFacing) {
        return newFacing != inputSide && newFacing != outputSide;
    }

    @Override
    public void onWatch() {
        Util.scheduleBlockUpdate(this);
    }

    @Override
    public void onEnergyNetUpdate() {
        if (getCurrent() > maxCurrent) {
            isOn = false;
            Energy.postTileRejoinEvent(this);
            Util.updateTileEntityField(this, "isOn");
        }
    }

    private float getCurrent() {
        if (!isOn)
            return 0;

        TileEntity neighbor;
        for (ForgeDirection dir : new ForgeDirection[] { inputSide, outputSide }) {
            neighbor = Util.getTileEntityonDirection(this, dir);
            if (neighbor instanceof IConductor) {
                return 2F * Math.abs((Energy.getVoltage((IConductor) neighbor) - (Energy.getVoltage(this))) /
                        (((IConductor) neighbor).getResistance() + this.getResistance()));
            }
        }
        return 0;
    }

	@Override
	public float getResistance(IBaseComponent neighbor) {
		return 0;
	}
}
