package simelectricity.essential.machines.tile;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.tileentity.INamedContainerProvider2;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.components.ISEDiode;
import simelectricity.essential.common.semachine.SETwoPortMachine;
import simelectricity.essential.machines.gui.ContainerDiode;

public class TileDiode extends SETwoPortMachine<ISEDiode> implements
		ISEDiode, ISEEnergyNetUpdateHandler, INamedContainerProvider2 {
    public TileDiode(BlockPos pos, BlockState blockState) {
		super(pos, blockState);
	}

	public double inputVoltage, outputVoltage;

    /////////////////////////////////////////////////////////
    ///ISEDiodeData
    /////////////////////////////////////////////////////////
    @Override
    public double getForwardResistance() {
        return 0.1;
    }

    @Override
    public double getSaturationCurrent() {
        return 1e-6;
    }

    @Override
    public double getThermalVoltage() {
        return 26e-6;
    }

    ///////////////////////////////////
    /// ISEEnergyNetUpdateHandler
    ///////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
        this.inputVoltage = this.input.getVoltage();
        this.outputVoltage = this.input.getComplement().getVoltage();
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
	public AbstractContainerMenu createMenu(int windowID, Inventory inv, Player player) {
		return new ContainerDiode(this, windowID);
	}
}
