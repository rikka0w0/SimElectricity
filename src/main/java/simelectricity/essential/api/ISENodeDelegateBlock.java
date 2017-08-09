package simelectricity.essential.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import simelectricity.api.node.ISESimulatable;

public interface ISENodeDelegateBlock {
	/**
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	ISESimulatable getNode(World world, BlockPos pos);
}
