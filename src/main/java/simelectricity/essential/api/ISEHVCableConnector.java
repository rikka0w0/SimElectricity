package simelectricity.essential.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import simelectricity.api.node.ISEGridNode;

public interface ISEHVCableConnector extends ISENodeDelegateBlock<ISEGridNode> {
    boolean canHVCableSelect(World world, BlockPos pos);
    
    default boolean canHVCableConnect(World world, BlockPos fromPos, BlockPos toPos) {
		return canHVCableSelect(world, fromPos);
    }
}