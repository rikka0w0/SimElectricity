package simelectricity.essential.machines.tile;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.tileentity.INamedContainerProvider2;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.semachine.SESinglePortMachine;
import simelectricity.essential.machines.gui.ContainerQuantumGenerator;

public class TileQuantumGenerator extends SESinglePortMachine<ISEVoltageSource> implements
		ISEVoltageSource, ISEEnergyNetUpdateHandler, INamedContainerProvider2 {
    public TileQuantumGenerator(BlockPos pos, BlockState blockState) {
		super(pos, blockState);
	}

	//Component parameters
    public double internalVoltage = 230;
    public double resistance = 0.1;

    //Calculated values
    public double voltage;
    public double current;

    ///////////////////////////////////
    /// BlockEntity
    ///////////////////////////////////
    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);

        this.internalVoltage = tagCompound.getDouble("internalVoltage");
        this.resistance = tagCompound.getDouble("resistance");
    }

    @Override
    public CompoundTag save(CompoundTag tagCompound) {
        tagCompound.putDouble("internalVoltage", this.internalVoltage);
        tagCompound.putDouble("resistance", this.resistance);

        return super.save(tagCompound);
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
    /// MenuProvider
    ///////////////////////////////////
	@Override
	public AbstractContainerMenu createMenu(int windowID, Inventory playerInv, Player player) {
		return new ContainerQuantumGenerator(this, windowID, player);
	}
}
