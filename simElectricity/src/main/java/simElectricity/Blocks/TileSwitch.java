package simElectricity.Blocks;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Energy;
import simElectricity.API.ISidedFacing;
import simElectricity.API.ISyncPacketHandler;
import simElectricity.API.Util;
import simElectricity.API.EnergyTile.IBaseComponent;
import simElectricity.API.EnergyTile.IConductor;
import simElectricity.API.EnergyTile.IConnectable;
import simElectricity.API.EnergyTile.IManualJunction;

public class TileSwitch extends TileEntity implements IManualJunction, IConnectable, ISyncPacketHandler, ISidedFacing{
    protected boolean isAddedToEnergyNet = false;
	
    public ForgeDirection inputSide = ForgeDirection.NORTH, outputSide = ForgeDirection.SOUTH , facing = ForgeDirection.WEST;
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
        }
    }

    @Override
    public void onServer2ClientUpdate(String field, Object value, short type) {   	
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
	@Override
	public float getResistance() {return resistance;}

	@Override
	public boolean canConnectOnSide(ForgeDirection side) {
		if (side == inputSide || side == outputSide)
			return true;
		return false;
	}

	@Override
	public void addNeighbors(List<IBaseComponent> list) {
		if (isOn){
			TileEntity neighbor = Util.getTEonDirection(this, inputSide);
		
			if (neighbor instanceof IConductor)
				list.add((IConductor)neighbor);
		
			neighbor = Util.getTEonDirection(this, outputSide);
		
			if (neighbor instanceof IConductor)
				list.add((IConductor)neighbor);	
		}
	}

	@Override
	public ForgeDirection getFacing() {return facing;}

	@Override
	public void setFacing(ForgeDirection newFacing) {facing = newFacing;}

	@Override
	public boolean canSetFacing(ForgeDirection newFacing) {
		if (newFacing != inputSide && newFacing != outputSide)
			return true;
		else
			return false;
	}
	
}
