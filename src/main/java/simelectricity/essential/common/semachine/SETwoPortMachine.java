package simelectricity.essential.common.semachine;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.Utils;
import simelectricity.api.ISESidedFacing;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEComponentParameter;
import simelectricity.api.node.ISEPairedComponent;
import simelectricity.api.node.ISESubComponent;
import simelectricity.api.tile.ISETile;

public abstract class SETwoPortMachine<T extends ISEComponentParameter> extends SEMachineTile implements
		ISESidedFacing, ISETile, ISEComponentParameter {
    public Direction inputSide = Direction.SOUTH;
    public Direction outputSide = Direction.NORTH;
    protected final ISEPairedComponent<?> input = (ISEPairedComponent<?>) SEAPI.energyNetAgent.newComponent(this, this);
    @SuppressWarnings("unchecked")
	protected final T cachedParam = (T) input;

    public SETwoPortMachine(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
		super(beType, pos, blockState);
	}

    ///////////////////////////////////
    /// BlockEntity
    ///////////////////////////////////
    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);

        this.inputSide = Utils.facingFromNbt(tagCompound, "inputSide");
        this.outputSide = Utils.facingFromNbt(tagCompound, "outputSide");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
    	Utils.saveToNbt(nbt, "inputSide", this.inputSide);
    	Utils.saveToNbt(nbt, "outputSide", this.outputSide);

    	super.saveAdditional(nbt);
    }

    @Override
    public Direction getFacing() {
    	return getBlockState().getValue(BlockStateProperties.FACING);
    }

    ///////////////////////////////////
    /// ISESidedFacing
    ///////////////////////////////////
    @Override
    public void setFacing(Direction newFacing) {
    	level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlockStateProperties.FACING, newFacing));
    }

    @Override
    public boolean canSetFacing(Direction newFacing) {
        return true;
    }

    /////////////////////////////////////////////////////////
    ///Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(CompoundTag nbt) {
        super.prepareS2CPacketData(nbt);

        Utils.saveToNbt(nbt, "inputSide", this.inputSide);
        Utils.saveToNbt(nbt, "outputSide", this.outputSide);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onSyncDataFromServerArrived(CompoundTag nbt) {
        this.inputSide = Utils.facingFromNbt(nbt, "inputSide");
        this.outputSide = Utils.facingFromNbt(nbt, "outputSide");

        // Flag 1 - update Rendering Only!
        this.markForRenderUpdate();

        super.onSyncDataFromServerArrived(nbt);
    }

    /////////////////////////////////////////////////////////
    ///ISETile
    /////////////////////////////////////////////////////////
    @Override
    public ISESubComponent<?> getComponent(Direction side) {
        if (side == this.inputSide)
            return this.input;
        else if (side == this.outputSide)
            return this.input.getComplement();
        else
            return null;
    }

    /////////////////////////////////////////////////////////
    /// Utils
    /////////////////////////////////////////////////////////
    public void setFunctionalSide(Direction input, Direction output) {
        inputSide = input;
        outputSide = output;

        if (level.isClientSide) {
        	this.markForRenderUpdate();
        	return;
        }

        markTileEntityForS2CSync();
        this.level.updateNeighborsAt(this.worldPosition, getBlockState().getBlock());
        //this.worldObj.notifyBlockChange(xCoord, yCoord, zCoord, this.getBlockType());

        if (isAddedToEnergyNet)
            SEAPI.energyNetAgent.updateTileConnection(this);
    }
}
