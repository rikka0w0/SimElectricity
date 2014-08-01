package simElectricity.API.Client;

import org.lwjgl.opengl.GL11;

public class RenderUtil {
    /**
     * Rotate the coordinate system so that the object in it can lay between two points
     *
     * @param xStart Start X coordinate
     * @param yStart Start Y coordinate
     * @param zStart Start Z coordinate
     * @param xEnd   End X coordinate
     * @param yEnd   End Y coordinate
     * @param zEnd   End Z coordinate
     */
    public static void p2pRotation(double xStart, double yStart, double zStart, double xEnd, double yEnd, double zEnd) {
        double distance = distanceOf(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        GL11.glRotated(Math.acos((yEnd - yStart) / distance) * 180 / Math.PI, (zEnd - zStart) / distance, 0, (xStart - xEnd) / distance);
        GL11.glRotated(Math.atan2(zStart - zEnd, xEnd - xStart) * 180 / Math.PI, 0, 1, 0);
    }

    /**
     * Calculate the distance between two points
     *
     * @param xStart Start X coordinate
     * @param yStart Start Y coordinate
     * @param zStart Start Z coordinate
     * @param xEnd   End X coordinate
     * @param yEnd   End Y coordinate
     * @param zEnd   End Z coordinate
     */
    public static double distanceOf(double xStart, double yStart, double zStart, double xEnd, double yEnd, double zEnd) {
        return Math.sqrt(Math.pow(xStart - xEnd, 2) +
                Math.pow(yStart - yEnd, 2) +
                Math.pow(zStart - zEnd, 2));
    }
}
