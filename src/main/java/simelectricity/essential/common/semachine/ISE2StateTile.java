package simelectricity.essential.common.semachine;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Only TileEntities should implement this!
 * @author Rikka0w0
 */
public interface ISE2StateTile {
    default void setSecondState(boolean val) {
    	BlockEntity te = (BlockEntity) this;
    	Level world = te.getLevel();
    	BlockPos pos = te.getBlockPos();
    	BlockState blockstate = te.getBlockState();

    	if (!hasSecondState(blockstate))
    		throw new RuntimeException("The second state does not exist for " + te.getClass().getCanonicalName());

    	world.setBlockAndUpdate(pos, blockstate.setValue(BlockStateProperties.POWERED, val));
    }
    
    public static boolean hasSecondState(BlockState state) {
		return state.hasProperty(BlockStateProperties.POWERED);
    }
}
