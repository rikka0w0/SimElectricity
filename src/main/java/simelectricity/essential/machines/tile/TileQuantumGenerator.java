package simelectricity.essential.machines.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.semachine.ISESocketProvider;
import simelectricity.essential.common.semachine.SESinglePortMachine;
import simelectricity.essential.machines.gui.ContainerQuantumGenerator;

public class TileQuantumGenerator extends SESinglePortMachine<ISEVoltageSource> implements ISEVoltageSource, ISEEnergyNetUpdateHandler, ISESocketProvider, INamedContainerProvider {
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
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);

        this.internalVoltage = tagCompound.getDouble("internalVoltage");
        this.resistance = tagCompound.getDouble("resistance");
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putDouble("internalVoltage", this.internalVoltage);
        tagCompound.putDouble("resistance", this.resistance);

        return super.write(tagCompound);
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

    @Override
    public boolean isOn() {
        return true;
    }

    ///////////////////////////////////
    /// ISEEnergyNetUpdateHandler
    ///////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
        this.voltage = this.circuit.getVoltage();

        //Get the resistance (in the state) when the voltage is calculated
        double internalVoltage = this.cachedParam.getOutputVoltage();
        double resistance = this.cachedParam.getResistance();
        this.current = (internalVoltage - this.voltage) / resistance;
    }

    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
    @Override
    @OnlyIn(Dist.CLIENT)
    public int getSocketIconIndex(Direction side) {
        return side == this.functionalSide ? 1 : -1;
    }
    
    ///////////////////////////////////
    /// INamedContainerProvider
    ///////////////////////////////////
	@Override
	public Container createMenu(int windowID, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerQuantumGenerator(this, windowID);
	}
}
