package simelectricity.essential.machines.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.tileentity.IGuiProviderTile;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.semachine.ISESocketProvider;
import simelectricity.essential.common.semachine.SESinglePortMachine;
import simelectricity.essential.machines.gui.ContainerQuantumGenerator;

public class TileQuantumGenerator extends SESinglePortMachine implements ISEVoltageSource, ISEEnergyNetUpdateHandler, ISESocketProvider, IGuiProviderTile {
    //Component parameters
    public volatile double internalVoltage = 230;
    public volatile double resistance = 0.1;

    //Calculated values
    public volatile double voltage;
    public volatile double current;

    ///////////////////////////////////
    /// TileEntity
    ///////////////////////////////////
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        this.internalVoltage = tagCompound.getDouble("internalVoltage");
        this.resistance = tagCompound.getDouble("resistance");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setDouble("internalVoltage", this.internalVoltage);
        tagCompound.setDouble("resistance", this.resistance);

        return super.writeToNBT(tagCompound);
    }

    ///////////////////////////////////
    /// ISEVoltageSource
    ///////////////////////////////////
    @Override
    public double getOutputVoltage() {
        return this.internalVoltage;
    }

    @Override
    public double getResistance() {
        return this.resistance;
    }

    ///////////////////////////////////
    /// ISEEnergyNetUpdateHandler
    ///////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
        this.voltage = SEAPI.energyNetAgent.getVoltage(circuit);

        //Get the resistance (in the state) when the voltage is calculated
        double internalVoltage = ((ISEVoltageSource) circuit).getOutputVoltage();
        double resistance = ((ISEVoltageSource) circuit).getResistance();
        this.current = (internalVoltage - this.voltage) / resistance;
    }

    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
    @Override
    @SideOnly(Side.CLIENT)
    public int getSocketIconIndex(EnumFacing side) {
        return side == this.functionalSide ? 1 : -1;
    }
    
    ///////////////////////////////////
    /// IGuiProviderTile
    ///////////////////////////////////
	@Override
	public Container getContainer(EntityPlayer player, EnumFacing side) {
		return new ContainerQuantumGenerator(this);
	}
}
