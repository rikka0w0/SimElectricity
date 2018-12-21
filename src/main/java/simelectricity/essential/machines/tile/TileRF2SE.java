package simelectricity.essential.machines.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.tileentity.IGuiProviderTile;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEConstantPowerSource;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.semachine.ISESocketProvider;
import simelectricity.essential.common.semachine.SESinglePortMachine;
import simelectricity.essential.machines.gui.ContainerRF2SE;

public class TileRF2SE extends SESinglePortMachine implements ISEConstantPowerSource, ISEEnergyNetUpdateHandler, ITickable, IGuiProviderTile, ISESocketProvider {
    //Component parameters
    public volatile double internalVoltage = 230;
    public volatile double resistance = 0.1;
    public boolean enabled = true;

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
        this.enabled = tagCompound.getBoolean("enabled");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setDouble("internalVoltage", this.internalVoltage);
        tagCompound.setDouble("resistance", this.resistance);
        tagCompound.setBoolean("enabled", this.enabled);

        return super.writeToNBT(tagCompound);
    }

    @Override
    public void update() {
        if (world.isRemote)
            return;

    }

    ///////////////////////////////////
    /// ISEEnergyNetUpdateHandler
    ///////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
        this.voltage = SEAPI.energyNetAgent.getVoltage(circuit);

        //Get the resistance (in the state) when the voltage is calculated
        //double internalVoltage = ((ISEConstantPowerSource) circuit).getOutputVoltage();
        //double resistance = ((ISEConstantPowerSource) circuit).getResistance();
        //this.current = (internalVoltage - this.voltage) / resistance;
    }

    ///////////////////////////////////
    /// ISEVoltageSource
    ///////////////////////////////////
    @Override
    public double getRatedPower() {
        return 500;
    }

    @Override
    public double getMinimumOutputVoltage() {
        return 90;
    }

    @Override
    public double getMaximumOutputVoltage() {
        return 265/2.0F;
    }

    @Override
    public boolean isOn() {
        return true;
    }

    ///////////////////////////////////
    /// IGuiProviderTile
    ///////////////////////////////////
    @Override
    public Container getContainer(EntityPlayer player, EnumFacing side) {
        return new ContainerRF2SE(this);
    }

    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
    @Override
    @SideOnly(Side.CLIENT)
    public int getSocketIconIndex(EnumFacing side) {
        return side == this.functionalSide ? 1 : -1;
    }
}
