package simelectricity.essential.machines.blockentity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import rikka.librikka.blockentity.INamedMenuProvider;
import rikka.librikka.blockentity.ITickableBlockEntity;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.components.ISESwitch;
import simelectricity.essential.common.semachine.SETwoPortMachine;
import simelectricity.essential.machines.gui.ContainerPowerMeter;

public class BlockEntityPowerMeter extends SETwoPortMachine<ISESwitch> implements
		ISESwitch, ISEEnergyNetUpdateHandler, ITickableBlockEntity, INamedMenuProvider {
    public BlockEntityPowerMeter(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
		super(beType, pos, blockState);
	}

	public boolean isOn;
    public double current, voltage, bufferedEnergy;

    ///////////////////////////////////
    /// BlockEntity
    ///////////////////////////////////
    @Override
    public void tick() {
        if (this.level.isClientSide)
            return;

        if (this.isOn)
            this.bufferedEnergy += voltage * current / 20;
    }

    @Override
    public void loadAdditional(CompoundTag tagCompound, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tagCompound, registries);

        this.bufferedEnergy = tagCompound.getDouble("bufferedEnergy");
        this.isOn = tagCompound.getBoolean("isOn");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider registries) {
    	nbt.putDouble("bufferedEnergy", this.bufferedEnergy);
    	nbt.putBoolean("isOn", this.isOn);

        super.saveAdditional(nbt, registries);
    }

    /////////////////////////////////////////////////////////
    ///ISEEnergyNetUpdateHandler
    /////////////////////////////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
        this.voltage = this.input.getVoltage();
        if (this.isOn) {
            this.current = (voltage - this.input.getComplement().getVoltage()) / this.cachedParam.getResistance();
        } else {
            this.current = 0;
        }
    }

    /////////////////////////////////////////////////////////
    ///ISESwitchData
    /////////////////////////////////////////////////////////
    @Override
    public boolean isOn() {
        return this.isOn;
    }

    @Override
    public double getResistance() {
        return 0.01;
    }

    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
    @Override
    @OnlyIn(Dist.CLIENT)
    public int getSocketIconIndex(Direction side) {
        if (side == this.inputSide)
            return 2;
        else if (side == this.outputSide)
            return 4;
        else
            return -1;
    }

    ///////////////////////////////////
    /// MenuProvider
    ///////////////////////////////////
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
        return new ContainerPowerMeter(this, windowId, player);
    }
}
