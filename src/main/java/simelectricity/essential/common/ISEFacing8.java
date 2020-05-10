package simelectricity.essential.common;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rikka.librikka.DirHorizontal8;

public interface ISEFacing8 {
	
	/**
	 * @param facing a number between 0 and 7 (inclusive), represents the rotation of the block,
	 * calculated from the yaw of the placer. </p>
	 * facing = 8 - MathHelper.floor(placer.rotationYaw * 8.0F / 360.0F + 0.5D) & 7;
	 */
	default boolean setFacing(DirHorizontal8 facing) {
		TileEntity te = (TileEntity) this;
		BlockPos pos = te.getPos();
		World world = te.getWorld();
		BlockState oldState = world.getBlockState(pos);
		BlockState newState = oldState.with(DirHorizontal8.prop, facing);
		return world.setBlockState(pos, newState);
	}
	
	/**
	 * @return a number between 0 and 7 (inclusive), represents the rotation
	 */
	default DirHorizontal8 getRotation() {
		TileEntity te = (TileEntity) this;
		BlockPos pos = te.getPos();
		World world = te.getWorld();
		BlockState state = world.getBlockState(pos);
		return state.get(DirHorizontal8.prop);
	}
}
