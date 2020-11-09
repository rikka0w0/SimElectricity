package simelectricity.essential.common.semachine;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Only TileEntities should implement this!
 * @author Rikka0w0
 */
public interface ISE2StateTile {
    default void setSecondState(boolean val) {
    	TileEntity te = (TileEntity) this;
    	World world = te.getWorld();
    	BlockPos pos = te.getPos();
    	BlockState blockstate = te.getBlockState();

    	if (!hasSecondState(blockstate))
    		throw new RuntimeException("The second state does not exist for " + te.getClass().getCanonicalName());

    	world.setBlockState(pos, blockstate.with(BlockStateProperties.POWERED, val));
    }
    
    public static boolean hasSecondState(BlockState state) {
		return state.hasProperty(BlockStateProperties.POWERED);
    }
}
