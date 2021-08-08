package simelectricity.essential.machines.tile;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import rikka.librikka.tileentity.ITickableBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.tileentity.INamedContainerProvider2;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.components.ISESwitch;
import simelectricity.essential.common.semachine.SETwoPortMachine;
import simelectricity.essential.machines.gui.ContainerPowerMeter;

public class TilePowerMeter extends SETwoPortMachine<ISESwitch> implements
		ISESwitch, ISEEnergyNetUpdateHandler, ITickableBlockEntity, INamedContainerProvider2 {
    public TilePowerMeter(BlockPos pos, BlockState blockState) {
		super(pos, blockState);
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
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);

        this.bufferedEnergy = tagCompound.getDouble("bufferedEnergy");
        this.isOn = tagCompound.getBoolean("isOn");
    }

    @Override
    public CompoundTag save(CompoundTag tagCompound) {
        tagCompound.putDouble("bufferedEnergy", this.bufferedEnergy);
        tagCompound.putBoolean("isOn", this.isOn);

        return super.save(tagCompound);
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
