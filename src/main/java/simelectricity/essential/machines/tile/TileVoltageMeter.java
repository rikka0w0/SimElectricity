package simelectricity.essential.machines.tile;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.blockentity.INamedMenuProvider;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.semachine.SESinglePortMachine;
import simelectricity.essential.machines.gui.ContainerVoltageMeter;

public class TileVoltageMeter extends SESinglePortMachine<ISEVoltageSource> implements
		ISEVoltageSource, ISEEnergyNetUpdateHandler, INamedMenuProvider {
    public TileVoltageMeter(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
		super(beType, pos, blockState);
	}

	public double voltage;

    @Override
    public double getResistance() {
        return 1e6F;
    }

    @Override
    public double getOutputVoltage() {
        return 0;
    }

    @Override
    public boolean isOn() {
        return true;
    }

    @Override
    public void onEnergyNetUpdate() {
        this.voltage = this.circuit.getVoltage();
    }


    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
    @Override
    @OnlyIn(Dist.CLIENT)
    public int getSocketIconIndex(Direction side) {
        return side == functionalSide ? 0 : -1;
    }

    ///////////////////////////////////
    /// MenuProvider
    ///////////////////////////////////
	@Override
	public AbstractContainerMenu createMenu(int windowID, Inventory playerInv, Player player) {
		return new ContainerVoltageMeter(this, windowID, player);
	}
}
