package simelectricity.essential.machines.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simelectricity.api.IEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.SESinglePortMachine;
import simelectricity.essential.machines.render.ISESocketProvider;

public class TileQuantumGenerator extends SESinglePortMachine implements ISEVoltageSource, IEnergyNetUpdateHandler, ISESocketProvider{
    //Component parameters
	public double internalVoltage = 230;
    public double resistance = 0.1;
    
    //Calculated values
    public double voltage;
    public double current;
	
	///////////////////////////////////
    /// TileEntity
	///////////////////////////////////
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        
        internalVoltage = tagCompound.getDouble("internalVoltage");
        resistance = tagCompound.getDouble("resistance");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        
        tagCompound.setDouble("internalVoltage", internalVoltage);
        tagCompound.setDouble("resistance", resistance);
    }

    ///////////////////////////////////
    /// ISEVoltageSource
    ///////////////////////////////////
	@Override
	public double getOutputVoltage() {
		return internalVoltage;
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
		double internalVoltage = ((ISEVoltageSource)this.circuit).getOutputVoltage();
		double resistance = ((ISEVoltageSource)this.circuit).getResistance();
		current = (internalVoltage - voltage)/resistance;
	}	
	
    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
	@Override
	@SideOnly(Side.CLIENT)
	public int getSocketIconIndex(ForgeDirection side) {
		return side == functionalSide ? 1 : -1;
	}
}
