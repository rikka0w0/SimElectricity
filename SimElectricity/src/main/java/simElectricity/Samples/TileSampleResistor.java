package simElectricity.Samples;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.IEnergyTile;
import simElectricity.API.Util;

public class TileSampleResistor extends TileSampleEnergyTile{
	public boolean isWorking=false;

	
	@Override
    public void readFromNBT(NBTTagCompound nbt)
    {
		super.readFromNBT(nbt);
    	isWorking=nbt.getBoolean("isWorking");
    }
    
    public void writeToNBT(NBTTagCompound nbt){
    	super.writeToNBT(nbt);
    	nbt.setBoolean("isWorking", isWorking);
    }
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote)
			return;
		float p=Util.getPower(this);
		if(p>0){
			if(!isWorking){
				isWorking=true;
				Util.updateTileEntityField(this, "isWorking");
				Util.scheduleBlockUpdate(this);
			}
		}
		else if(isWorking){
			isWorking=false;
			Util.updateTileEntityField(this, "isWorking");
			Util.scheduleBlockUpdate(this);
		}
		
		//if (isWorking)
			//worldObj.createExplosion(null, xCoord, yCoord, zCoord, 10, true);
		
	}
	
	@Override
	public float getResistance() {
		return 10000;
	}

	@Override
	public void onOverloaded() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getMaxPowerDissipation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getMaxSafeVoltage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onOverVoltage() {
		// TODO Auto-generated method stub

	}

	@Override
	public float getOutputVoltage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ForgeDirection getFunctionalSide() {
		return functionalSide;
	}
}
