package simelectricity.essential.utils;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by manageryzy on 12/18/2017.
 */
public class RedstoneHelper {
    public static boolean isBlockPowered(World world, BlockPos pos, int threshold) {
        return world.getRedstonePower(pos.down(), Direction.DOWN) > threshold ? true : (world.getRedstonePower(pos.up(), Direction.UP) > threshold ? true : (world.getRedstonePower(pos.north(), Direction.NORTH) > threshold ? true : (world.getRedstonePower(pos.south(), Direction.SOUTH) > threshold ? true : (world.getRedstonePower(pos.west(), Direction.WEST) > threshold ? true : world.getRedstonePower(pos.east(), Direction.EAST) > threshold))));
    }
}
