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
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.components.ISETransformer;
import simelectricity.essential.common.semachine.SETwoPortMachine;
import simelectricity.essential.machines.gui.ContainerAdjustableTransformer;

public class BlockEntityAdjustableTransformer extends SETwoPortMachine<ISETransformer> implements
		ISETransformer, ISEEnergyNetUpdateHandler, INamedMenuProvider {
    public BlockEntityAdjustableTransformer(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
		super(beType, pos, blockState);
	}

	//Input - primary, output - secondary
    public double ratio = 10, outputResistance = 1;

    public double vPri, vSec;

    /////////////////////////////////////////////////////////
    ///BlockEntity
    /////////////////////////////////////////////////////////
    @Override
    public void loadAdditional(CompoundTag tagCompound, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tagCompound, registries);

		this.ratio = tagCompound.getDouble("ratio");
		this.outputResistance = tagCompound.getDouble("outputResistance");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider registries) {
    	nbt.putDouble("ratio", this.ratio);
    	nbt.putDouble("outputResistance", this.outputResistance);

        super.saveAdditional(nbt, registries);
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
