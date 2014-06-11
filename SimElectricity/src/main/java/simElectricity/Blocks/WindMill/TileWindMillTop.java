package simElectricity.Blocks.WindMill;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;


import simElectricity.API.Util;
import simElectricity.API.Common.TileSidedFacingMachine;

public class TileWindMillTop extends TileSidedFacingMachine{
	public int randAngle=(new Random()).nextInt(180);
	public boolean settled,initiallized;
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote && !initiallized) {
			this.initiallized=true;
			Util.scheduleBlockUpdate(this);
		}
	}
	
	@Override
	public boolean canSetFacing(ForgeDirection newFacing) {
		if(newFacing!=ForgeDirection.UP&&newFacing!=ForgeDirection.DOWN)
			return true;
		else
			return false;
	}
	
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
    	super.readFromNBT(tagCompound);
    	settled=tagCompound.getBoolean("settled");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
    	super.writeToNBT(tagCompound);  
    	tagCompound.setBoolean("settled", settled);
    }
	
	@Override
	public int getInventorySize() {return 0;}

}
