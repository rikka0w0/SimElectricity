package simelectricity.essential.grid.transformer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import simelectricity.essential.common.SEMultiBlockEnergyTile;

public class TilePowerTransformerPlaceHolder extends SEMultiBlockEnergyTile {
    public TilePowerTransformerPlaceHolder(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
		super(beType, pos, blockState);
	}

    @Override
    public void onLoad() {}
	@Override
	protected void onStructureCreating() {}
    @Override
    public void onStructureCreated() {}
    @Override
    public void onStructureRemoved() {}

    public static class Primary extends TilePowerTransformerPlaceHolder {
        public Primary(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
    		super(beType, pos, blockState);
    	}

        public TilePowerTransformerWinding getWinding() {
            BlockPos pos = this.mbInfo.getPartPos(EnumPowerTransformerBlockType.Primary.offset);
            BlockEntity te = this.level.getBlockEntity(pos);
            return te instanceof TilePowerTransformerWinding ? (TilePowerTransformerWinding)te : null;
        }
    }

    public static class Secondary extends TilePowerTransformerPlaceHolder {
        public Secondary(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
    		super(beType, pos, blockState);
    	}

        public TilePowerTransformerWinding getWinding() {
            BlockPos pos = this.mbInfo.getPartPos(EnumPowerTransformerBlockType.Secondary.offset);
            BlockEntity te = this.level.getBlockEntity(pos);
            return te instanceof TilePowerTransformerWinding ? (TilePowerTransformerWinding) te : null;
        }
    }

    public static class Render extends TilePowerTransformerPlaceHolder {
        public Render(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
    		super(beType, pos, blockState);
    	}

        @Override
        @OnlyIn(Dist.CLIENT)
        public void onSyncDataFromServerArrived(CompoundTag nbt) {
        	super.onSyncDataFromServerArrived(nbt);
            markForRenderUpdate();
        }

        //////////////////////////////
        /////BlockEntity
        //////////////////////////////
        @Override
        @OnlyIn(Dist.CLIENT)
        public double getViewDistance() {
            return 100000;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public AABB getRenderBoundingBox() {
            return BlockEntity.INFINITE_EXTENT_AABB;
        }

        public boolean hasFastRenderer() {
            return true;
        }
    }
}
