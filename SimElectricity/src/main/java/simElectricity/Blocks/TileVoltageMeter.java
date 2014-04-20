package simElectricity.Blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.IEnergyTile;
import simElectricity.API.ISyncPacketHandler;
import simElectricity.API.Util;

public class TileVoltageMeter extends TileEntity implements IEnergyTile,ISyncPacketHandler{
	public ForgeDirection functionalSide=ForgeDirection.NORTH;
	protected boolean isAddedToEnergyNet = false;
	
	public float voltage=0;
	
	@Override
	public void onServer2ClientUpdate(String field, Object value, short type) {
		if(field.contains("functionalSide"))	
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public void onClient2ServerUpdate(String field, Object value, short type) {}
	
	@Override
	public void updateEntity() {
		if (!worldObj.isRemote && !isAddedToEnergyNet) {
			Util.postTileAttachEvent(this);
			this.isAddedToEnergyNet=true;
			Util.scheduleBlockUpdate(this);
		}
	}

	@Override
	public void invalidate() {
		if (!worldObj.isRemote & isAddedToEnergyNet)
			Util.postTileDetachEvent(this);
	}
	
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
    	super.readFromNBT(tagCompound);
    	
    	functionalSide=Util.byte2Direction(tagCompound.getByte("functionalSide"));
    }
    
    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
    	super.writeToNBT(tagCompound);
    	
    	tagCompound.setByte("functionalSide", Util.direction2Byte(functionalSide));
    }
	
    @Override
	public float getResistance() {return 1000000;}

	@Override
	public void onOverloaded() {}

	@Override
	public int getMaxPowerDissipation() {return 0;}

	@Override
	public float getOutputVoltage() {return 0;}

	@Override
	public float getMaxSafeVoltage() {return 0;}

	@Override
	public void onOverVoltage() {}

	@Override
	public ForgeDirection getFunctionalSide() {return functionalSide;}
}
