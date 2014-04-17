package simElectricity.Blocks;

import simElectricity.API.IEnergyTile;
import simElectricity.API.TileAttachEvent;
import simElectricity.API.TileDetachEvent;
import simElectricity.API.Util;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class TileQuantumGenerator extends TileEntity implements IEnergyTile{
	public ForgeDirection functionalSide=ForgeDirection.NORTH;
	protected boolean isAddedToEnergyNet = false;
	
	@Override
	public void updateEntity() {
		if (!worldObj.isRemote && !isAddedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new TileAttachEvent(this));
			this.isAddedToEnergyNet=true;
			Util.scheduleBlockUpdate(this);
		}
	}

	@Override
	public void invalidate() {
		if (!worldObj.isRemote & isAddedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new TileDetachEvent(this));
		}
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
	public float getResistance() {return 1;}

	@Override
	public void onOverloaded() {}

	@Override
	public int getMaxPowerDissipation() {return 0;}

	@Override
	public float getOutputVoltage() {return 12;}

	@Override
	public float getMaxSafeVoltage() {return 0;}

	@Override
	public void onOverVoltage() {}

	@Override
	public ForgeDirection getFunctionalSide() {return functionalSide;}
}
