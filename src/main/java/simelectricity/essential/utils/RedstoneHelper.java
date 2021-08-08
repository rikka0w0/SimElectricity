package simelectricity.essential.utils;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Created by manageryzy on 12/18/2017.
 */
public class RedstoneHelper {
    public static boolean isBlockPowered(Level world, BlockPos pos, int threshold) {
        return world.getSignal(pos.below(), Direction.DOWN) > threshold ? true : (world.getSignal(pos.above(), Direction.UP) > threshold ? true : (world.getSignal(pos.north(), Direction.NORTH) > threshold ? true : (world.getSignal(pos.south(), Direction.SOUTH) > threshold ? true : (world.getSignal(pos.west(), Direction.WEST) > threshold ? true : world.getSignal(pos.east(), Direction.EAST) > threshold))));
    }
}
