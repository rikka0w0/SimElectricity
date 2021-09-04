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
import simelectricity.api.ISEWrenchable;
import simelectricity.api.ISESidedFacing;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEComponentParameter;
import simelectricity.api.node.ISESubComponent;
import simelectricity.api.tile.ISETile;

public abstract class SESinglePortMachine<T extends ISEComponentParameter> extends SEMachineTile implements
		ISESidedFacing, ISEWrenchable, ISETile, ISEComponentParameter {
    protected Direction functionalSide = Direction.SOUTH;
    protected final ISESubComponent<?> circuit = SEAPI.energyNetAgent.newComponent(this, this);
    @SuppressWarnings("unchecked")
	protected final T cachedParam = (T) circuit;

    public SESinglePortMachine(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
		super(beType, pos, blockState);
	}

    ///////////////////////////////////
    /// BlockEntity
    ///////////////////////////////////
    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
        this.functionalSide = Utils.facingFromNbt(tagCompound, "functionalSide");
    }

    @Override
    public CompoundTag save(CompoundTag tagCompound) {
        Utils.saveToNbt(tagCompound, "functionalSide", this.functionalSide);
        return super.save(tagCompound);
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


    ///////////////////////////////////
    /// ISEWrenchable
    ///////////////////////////////////
    @Override
    public void onWrenchAction(Direction side, boolean isCreativePlayer) {
        this.SetFunctionalSide(side);
    }

    @Override
    public boolean canWrenchBeUsed(Direction side) {
        return true;
    }

    /////////////////////////////////////////////////////////
    ///Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(CompoundTag nbt) {
        super.prepareS2CPacketData(nbt);
        Utils.saveToNbt(nbt, "functionalSide", this.functionalSide);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onSyncDataFromServerArrived(CompoundTag nbt) {
        this.functionalSide = Utils.facingFromNbt(nbt, "functionalSide");
        super.onSyncDataFromServerArrived(nbt);
    }

    /////////////////////////////////////////////////////////
    ///ISETile
    /////////////////////////////////////////////////////////
    @Override
    public ISESubComponent<?> getComponent(Direction side) {
        return side == this.functionalSide ? this.circuit : null;
    }

    /////////////////////////////////////////////////////////
    ///Utils
    /////////////////////////////////////////////////////////
    public void SetFunctionalSide(Direction side) {
        this.functionalSide = side;

        if (level.isClientSide) {
        	this.markForRenderUpdate();
        	return;
        }

        markTileEntityForS2CSync();
        this.level.updateNeighborsAt(this.worldPosition, getBlockState().getBlock());

        if (isAddedToEnergyNet)
            SEAPI.energyNetAgent.updateTileConnection(this);
    }
}
