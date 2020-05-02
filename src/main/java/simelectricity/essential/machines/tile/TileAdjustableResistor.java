package simelectricity.essential.machines.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.semachine.ISESocketProvider;
import simelectricity.essential.common.semachine.SESinglePortMachine;
import simelectricity.essential.machines.gui.ContainerAdjustableResistor;

public class TileAdjustableResistor extends SESinglePortMachine<ISEVoltageSource> implements ISEVoltageSource, ISEEnergyNetUpdateHandler, ISESocketProvider, ITickableTileEntity, INamedContainerProvider {
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
    public void tick() {
        if (this.world.isRemote)
            return;

        this.bufferedEnergy += this.powerLevel / 20;
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);

        this.resistance = tagCompound.getDouble("resistance");
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putDouble("resistance", this.resistance);

        return super.write(tagCompound);
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
        double resistance = this.cachedParam.getResistance();
        this.current = this.voltage / resistance;
        this.powerLevel = this.voltage * this.current;
    }

    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
    @Override
    @OnlyIn(Dist.CLIENT)
    public int getSocketIconIndex(Direction side) {
        return side == this.functionalSide ? 0 : -1;
    }
    
    ///////////////////////////////////
    /// INamedContainerProvider
    ///////////////////////////////////
	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
		return new ContainerAdjustableResistor(this, windowId);
	}
}
