package simelectricity.essential.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import simelectricity.api.node.ISESimulatable;
import simelectricity.api.node.NoComplementException;

public interface ISENodeDelegateBlock {
    /**
     * @param world
     * @return
     * @throws NoComplementException
     */
    ISESimulatable getNode(World world, BlockPos pos);
}
