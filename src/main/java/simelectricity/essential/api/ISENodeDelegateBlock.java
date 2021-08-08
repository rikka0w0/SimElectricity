package simelectricity.essential.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import simelectricity.api.node.ISESimulatable;

public interface ISENodeDelegateBlock<T extends ISESimulatable> {
    /**
     * @param world
     * @param pos
     * @return
     * @throws NoComplementException
     */
    T getNode(Level world, BlockPos pos);
}
