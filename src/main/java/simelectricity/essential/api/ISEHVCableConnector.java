package simelectricity.essential.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISEHVCableConnector extends ISENodeDelegateBlock {
    boolean canHVCableConnect(World world, BlockPos pos);
}
