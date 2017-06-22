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

public class TileQuantumGenerator extends SESinglePortMachine<ISEVoltageSource> implements ISEVoltageSource, IEnergyNetUpdateHandler, ISESocketProvider{
    public double voltage = 0;
    private double internalVoltage = 230;
    private double resistance = 0.1;
	
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
