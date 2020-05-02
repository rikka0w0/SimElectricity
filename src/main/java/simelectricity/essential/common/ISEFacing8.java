package simelectricity.essential.common;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rikka.librikka.DirHorizontal8;

public interface ISEFacing8 {
	World getWorld();
	BlockPos getPos();
	
	/**
	 * @param facing a number between 0 and 7 (inclusive), represents the rotation of the block,
	 * calculated from the yaw of the placer. </p>
	 * facing = 8 - MathHelper.floor(placer.rotationYaw * 8.0F / 360.0F + 0.5D) & 7;
	 */
	default boolean setFacing(DirHorizontal8 facing) {
		BlockPos pos = this.getPos();
		World world = this.getWorld();
		BlockState oldState = world.getBlockState(pos);
		BlockState newState = oldState.with(DirHorizontal8.prop, facing);
		return world.setBlockState(pos, newState);
	}
	
	/**
	 * @return a number between 0 and 7 (inclusive), represents the rotation
	 */
	default DirHorizontal8 getRotation() {
		BlockPos pos = this.getPos();
		World world = this.getWorld();
		BlockState state = world.getBlockState(pos);
		return state.get(DirHorizontal8.prop);
	}
}
