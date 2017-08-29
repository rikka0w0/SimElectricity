package rikka.librikka;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

/**
 * BuildCraft magic
 *
 * @author Rikka0_0
 */
public class RayTraceHelper {
    /**
     * @param pos
     * @param start       absolute coordinate
     * @param end         absolute coordinate
     * @param boundingBox normal range: 0,0,0 - 1,1,1
     * @return
     */
    public static RayTraceResult rayTrace(BlockPos pos, Vec3d start, Vec3d end, AxisAlignedBB boundingBox) {
        Vec3d vec3d = start.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
        Vec3d vec3d1 = end.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
        RayTraceResult raytraceresult = boundingBox.calculateIntercept(vec3d, vec3d1);
        return raytraceresult == null ? null : new RayTraceResult(raytraceresult.hitVec.addVector((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()), raytraceresult.sideHit, pos);
    }

    /**
     * Perform raytrace, if new hit point is closer then return the new raytrace result, otherwise discard and return the last result.
     *
     * @param lastBest
     * @param pos
     * @param start
     * @param end
     * @param aabb
     * @param part
     * @return
     */
    public static RayTraceResult computeTrace(RayTraceResult lastBest, BlockPos pos, Vec3d start, Vec3d end,
                                              AxisAlignedBB aabb, int part) {
        RayTraceResult next = RayTraceHelper.rayTrace(pos, start, end, aabb);
        if (next == null)
            return lastBest;    //No intersection

        next.subHit = part;

        if (lastBest == null)
            return next;        //First intersection

        //The distance from start to the hit point
        double distLast = lastBest.hitVec.squareDistanceTo(start);
        double distNext = next.hitVec.squareDistanceTo(start);
        return distLast > distNext ? next : lastBest;    //Return the closer one
    }
    
    /**
     * A helper function for creating collision boxes, default facing: upwards, origin: block center
     * @param side
     * @param xStart
     * @param yStart
     * @param zStart
     * @param xEnd
     * @param yEnd
     * @param zEnd
     * @return
     */
    public static AxisAlignedBB createAABB (EnumFacing side, float xStart, float yStart, float zStart, float xEnd, float yEnd, float zEnd) {
        switch (side) {
		case DOWN:
			return new AxisAlignedBB(xStart, yStart, zStart, xEnd, yEnd, zEnd);
		case UP:
			return new AxisAlignedBB(xStart, 1-yEnd, zStart, xEnd, 1-yStart, zEnd);
		case NORTH:
			return new AxisAlignedBB(zStart, xStart, yStart, zEnd, xEnd, yEnd);
		case SOUTH:
			return new AxisAlignedBB(zStart, xStart, 1-yEnd, zEnd, xEnd, 1-yStart);
		case WEST:
			return new AxisAlignedBB(yStart, xStart, zStart, yEnd, xEnd, zEnd);
		case EAST:
			return new AxisAlignedBB(1-yEnd, xStart, zStart, 1-yStart, xEnd, zEnd);
        }
        return null;
    }
}
