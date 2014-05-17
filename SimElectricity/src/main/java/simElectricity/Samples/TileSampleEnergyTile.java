package simElectricity.Samples;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Util;
import simElectricity.API.EnergyTile.IEnergyTile;

public abstract class TileSampleEnergyTile extends TileSampleBaseComponent implements IEnergyTile {
	public ForgeDirection functionalSide=ForgeDirection.NORTH;
	
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
}
