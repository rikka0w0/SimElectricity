package simelectricity.essential.machines.tile;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.blockentity.INamedMenuProvider;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.components.ISETransformer;
import simelectricity.essential.common.semachine.SETwoPortMachine;
import simelectricity.essential.machines.gui.ContainerAdjustableTransformer;

public class TileAdjustableTransformer extends SETwoPortMachine<ISETransformer> implements
		ISETransformer, ISEEnergyNetUpdateHandler, INamedMenuProvider {
    public TileAdjustableTransformer(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
		super(beType, pos, blockState);
	}

	//Input - primary, output - secondary
    public double ratio = 10, outputResistance = 1;

    public double vPri, vSec;

    /////////////////////////////////////////////////////////
    ///BlockEntity
    /////////////////////////////////////////////////////////
    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);

		this.ratio = tagCompound.getDouble("ratio");
		this.outputResistance = tagCompound.getDouble("outputResistance");
    }

    @Override
    public CompoundTag save(CompoundTag tagCompound) {
        tagCompound.putDouble("ratio", this.ratio);
        tagCompound.putDouble("outputResistance", this.outputResistance);

        return super.save(tagCompound);
    }

    /////////////////////////////////////////////////////////
    ///ISEEnergyNetUpdateHandler
    /////////////////////////////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
		this.vPri = this.input.getVoltage();
		this.vSec = this.input.getComplement().getVoltage();
    }

    /////////////////////////////////////////////////////////
    ///ISETransformerData
    /////////////////////////////////////////////////////////
    @Override
    public double getRatio() {
        return this.ratio;
    }

    @Override
    public double getInternalResistance() {
        return this.outputResistance;
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
            return 3;
        else
            return -1;
    }

    ///////////////////////////////////
    /// MenuProvider
    ///////////////////////////////////
	@Override
	public AbstractContainerMenu createMenu(int windowID, Inventory inv, Player player) {
		return new ContainerAdjustableTransformer(this, windowID, player);
	}
}
