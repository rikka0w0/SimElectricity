package simelectricity.extension.facades;

import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class VecUtil {
    public static double getValue(Vec3d from, Axis axis) {
        return axis == Axis.X ? from.x : axis == Axis.Y ? from.y : from.z;
    }

    public static int getValue(Vec3i from, Axis axis) {
        return axis == Axis.X ? from.getX() : axis == Axis.Y ? from.getY() : from.getZ();
    }
}
