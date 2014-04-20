package simElectricity.Blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simElectricity.API.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class TileQuantumGenerator extends TileEntity implements IEnergyTile,ISyncPacketHandler{
	public ForgeDirection functionalSide=ForgeDirection.NORTH;
	protected boolean isAddedToEnergyNet = false;
	
	public float outputVoltage=12;
	public float outputResistance=1;
	
	
	@Override
	public void onServer2ClientUpdate(String field, Object value, short type) {
		if(field.contains("functionalSide"))	
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public void onClient2ServerUpdate(String field, Object value, short type) {
		if(field.contains("outputVoltage"))	
			Util.postTileChangeEvent(this);
	}
	
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
}
