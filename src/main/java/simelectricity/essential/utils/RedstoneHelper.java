package simelectricity.essential.utils;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by manageryzy on 12/18/2017.
 */
public class RedstoneHelper {
    public static boolean isBlockPowered(World world, BlockPos pos, int threshold) {
        return world.getRedstonePower(pos.down(), EnumFacing.DOWN) > threshold ? true : (world.getRedstonePower(pos.up(), EnumFacing.UP) > threshold ? true : (world.getRedstonePower(pos.north(), EnumFacing.NORTH) > threshold ? true : (world.getRedstonePower(pos.south(), EnumFacing.SOUTH) > threshold ? true : (world.getRedstonePower(pos.west(), EnumFacing.WEST) > threshold ? true : world.getRedstonePower(pos.east(), EnumFacing.EAST) > threshold))));
    }
}
