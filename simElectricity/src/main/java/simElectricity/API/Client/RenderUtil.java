package simElectricity.API.Client;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderUtil {
	public static void facingRotate(int facing) {
		GL11.glRotatef(getDirection(facing)*270F + 90F, 0F, 1F, 0F);
	}
	
    public static int getDirection(int facing) {
        switch (facing) {
            case 2: //N
                return 0;
            case 5: //E
                return 1;
            case 3: //S
                return 2;
            default://W
                return 3;
        }
    }
	
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
    
    /**
     * Calculate the distance between two points
     *
     * @param xStart Start X coordinate
     * @param zStart Start Z coordinate
     * @param xEnd   End X coordinate
     * @param zEnd   End Z coordinate
     */
    public static double distanceOf(double xStart, double zStart, double xEnd, double zEnd) {
        return Math.sqrt(Math.pow(xStart - xEnd, 2) +
                Math.pow(zStart - zEnd, 2));
    }
}
