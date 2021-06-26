package simelectricity.essential.client.coverpanel;

import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

public class VecUtil {
    public static double getValue(Vector3d from, Axis axis) {
        return axis == Axis.X ? from.x : axis == Axis.Y ? from.y : from.z;
    }

    public static int getValue(Vector3i from, Axis axis) {
        return axis == Axis.X ? from.getX() : axis == Axis.Y ? from.getY() : from.getZ();
    }
}
