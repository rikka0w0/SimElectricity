package simelectricity.essential.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import simelectricity.api.node.ISESimulatable;
import simelectricity.api.node.NoComplementException;

public interface ISENodeDelegateBlock<T extends ISESimulatable> {
    /**
     * @param world
     * @param pos
     * @return
     * @throws NoComplementException
     */
    T getNode(World world, BlockPos pos);
}
