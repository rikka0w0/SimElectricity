package simelectricity.essential.machines.tile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.api.IEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.SESinglePortMachine;
import simelectricity.essential.machines.render.ISESocketProvider;

public class TileAdjustableResistor extends SESinglePortMachine<ISEVoltageSource> implements ISEVoltageSource, IEnergyNetUpdateHandler, ISESocketProvider{
	//Component parameters
	public double resistance = 100;
	
	//Calculated values
	public double voltage;
	public double current;
	public double powerLevel;
	public double bufferedEnergy;
	
	///////////////////////////////////
    /// TileEntity
	///////////////////////////////////
	@Override
    public void updateEntity() {
        super.updateEntity();
        
        if (worldObj.isRemote)
        	return;
        
        bufferedEnergy += powerLevel/20;
	}
	
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        
        resistance = tagCompound.getDouble("resistance");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        
        tagCompound.setDouble("resistance", resistance);
    }
    
    ///////////////////////////////////
    /// ISEVoltageSource
    ///////////////////////////////////
	@Override
	public double getOutputVoltage() {
		return 0;
	}

	@Override
	public double getResistance() {
		return resistance;
	}

    ///////////////////////////////////
    /// IEnergyNetUpdateHandler
    ///////////////////////////////////
	@Override
	public void onEnergyNetUpdate() {
		voltage = SEAPI.energyNetAgent.getVoltage(this.circuit);
		
		//Get the resistance (in the state) when the voltage is calculated
		double resistance = ((ISEVoltageSource)this.circuit).getResistance();
		current = voltage/resistance;
		powerLevel = voltage*current;
	}	
	
    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
	@Override
	@SideOnly(Side.CLIENT)
	public int getSocketIconIndex(ForgeDirection side) {
		return side == functionalSide ? 0 : -1;
	}
}
