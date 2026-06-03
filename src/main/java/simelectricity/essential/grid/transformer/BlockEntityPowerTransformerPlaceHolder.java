package simelectricity.essential.grid.transformer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import simelectricity.essential.common.SEMultiBlockEnergyBlockEntity;

public class BlockEntityPowerTransformerPlaceHolder extends SEMultiBlockEnergyBlockEntity {
    public BlockEntityPowerTransformerPlaceHolder(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
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

    public static class Primary extends BlockEntityPowerTransformerPlaceHolder {
        public Primary(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
    		super(beType, pos, blockState);
    	}

        public BlockEntityPowerTransformerWinding getWinding() {
            BlockPos pos = this.mbInfo.getPartPos(EnumPowerTransformerBlockType.Primary.offset);
            BlockEntity te = this.level.getBlockEntity(pos);
            return te instanceof BlockEntityPowerTransformerWinding ? (BlockEntityPowerTransformerWinding)te : null;
        }
    }

    public static class Secondary extends BlockEntityPowerTransformerPlaceHolder {
        public Secondary(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
    		super(beType, pos, blockState);
    	}

        public BlockEntityPowerTransformerWinding getWinding() {
            BlockPos pos = this.mbInfo.getPartPos(EnumPowerTransformerBlockType.Secondary.offset);
            BlockEntity te = this.level.getBlockEntity(pos);
            return te instanceof BlockEntityPowerTransformerWinding ? (BlockEntityPowerTransformerWinding) te : null;
        }
    }

    public static class Render extends BlockEntityPowerTransformerPlaceHolder {
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


        public boolean hasFastRenderer() {
            return true;
        }
    }
}
