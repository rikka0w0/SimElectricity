package simelectricity.essential.common.semachine;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISE2StateTile {
	World getWorld();
	BlockPos getPos();
	BlockState getBlockState();
	
    default void setSecondState(boolean val) {
    	getWorld().setBlockState(getPos(), getBlockState().with(BlockStateProperties.POWERED, val));
    }
    
    public static boolean hasSecondState(BlockState state) {
		return state.has(BlockStateProperties.POWERED);
    }
}
