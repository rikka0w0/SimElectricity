package simElectricity.API.Common;

import simElectricity.API.Util;
import simElectricity.API.EnergyTile.IEnergyTile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileStandardGenerator extends TileEntity implements IEnergyTile{
	private ForgeDirection functionalSide=ForgeDirection.NORTH;
	private boolean isAddedToEnergyNet = false;
	
	public float outputVoltage=12;
	public float outputResistance=0.01F;
	
	public void init() {}
	
	//TileEntity--------------------------------------------------------------------------
	@Override
	public void updateEntity() {
		if (!worldObj.isRemote && !isAddedToEnergyNet) {
			Util.postTileAttachEvent(this);
			this.isAddedToEnergyNet=true;
			init();			
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
    	outputVoltage=tagCompound.getFloat("outputVoltage");
    	outputResistance=tagCompound.getFloat("outputResistance");
    }
    
    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
    	super.writeToNBT(tagCompound);
    	
    	tagCompound.setByte("functionalSide", Util.direction2Byte(functionalSide));
    	tagCompound.setFloat("outputVoltage", outputVoltage);
    	tagCompound.setFloat("outputResistance", outputResistance);
    }

	//IEnergyTile---------------------------------------------------------------------------
    @Override
	public float getResistance() {return outputResistance;}

	@Override
	public void onOverloaded() {}

	@Override
	public int getMaxPowerDissipation() {return 0;}

	@Override
	public float getOutputVoltage() {return outputVoltage;}

	@Override
	public float getMaxSafeVoltage() {return 0;}

	@Override
	public void onOverVoltage() {}

	@Override
	public ForgeDirection getFunctionalSide() {return functionalSide;}

	@Override
	public void setFunctionalSide(ForgeDirection newFunctionalSide) {functionalSide=newFunctionalSide;}

	@Override
	public boolean canSetFunctionalSide(ForgeDirection newFunctionalSide) {return true;}
}
