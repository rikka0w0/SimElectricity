package simelectricity.essential.machines.tile;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import simelectricity.api.IEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.semachine.ISESocketProvider;
import simelectricity.essential.common.semachine.SESinglePortMachine;

public class TileAdjustableResistor extends SESinglePortMachine implements ISEVoltageSource, IEnergyNetUpdateHandler, ISESocketProvider, ITickable{
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
    public void update() {
        if (world.isRemote)
        	return;
        
        bufferedEnergy += powerLevel/20;
	}
	
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        
        resistance = tagCompound.getDouble("resistance");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {       
        tagCompound.setDouble("resistance", resistance);
        
        return super.writeToNBT(tagCompound);
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
	public int getSocketIconIndex(EnumFacing side) {
		return side == functionalSide ? 0 : -1;
	}
}
