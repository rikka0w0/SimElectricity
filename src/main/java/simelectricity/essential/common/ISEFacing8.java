package simelectricity.essential.common;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import rikka.librikka.DirHorizontal8;

public interface ISEFacing8 {
	
	/**
	 * @param facing a number between 0 and 7 (inclusive), represents the rotation of the block,
	 * calculated from the yaw of the placer. </p>
	 * facing = 8 - Mth.floor(placer.rotationYaw * 8.0F / 360.0F + 0.5D) & 7;
	 */
	default boolean setFacing(DirHorizontal8 facing) {
		BlockEntity te = (BlockEntity) this;
		BlockPos pos = te.getBlockPos();
		Level world = te.getLevel();
		BlockState oldState = te.getBlockState();
		BlockState newState = oldState.setValue(DirHorizontal8.prop, facing);
		return world.setBlockAndUpdate(pos, newState);
	}
	
	/**
	 * @return a number between 0 and 7 (inclusive), represents the rotation
	 */
	default DirHorizontal8 getRotation() {
		BlockEntity te = (BlockEntity) this;
		BlockState state = te.getBlockState();
		return state.getValue(DirHorizontal8.prop);
	}
}
