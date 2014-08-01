package simElectricity.API.Client;

import org.lwjgl.opengl.GL11;

public class RenderUtil {
	/**
	 * Rotate the coordinate system so that the object in it can lay between two points
	 * @param x0 Start X coordinate
	 * @param y0 Start Y coordinate
	 * @param z0 Start Z coordinate
	 * @param x1 End X coordinate
	 * @param y1 End Y coordinate
	 * @param z1 End Z coordinate
	 */
    public static void p2pRotation(double x0, double y0, double z0, double x1, double y1, double z1){
    	double distance=distanceOf(x0,y0,z0,x1,y1,z1);
		GL11.glRotated(Math.acos((y1 - y0) / distance) * 180 / Math.PI,(z1 - z0) / distance, 0, (x0 - x1)/distance);	
		GL11.glRotated(Math.atan2(z0 - z1,x1 - x0) * 180 / Math.PI, 0, 1, 0);
    }
    
    /**
     * Calculate the distance between two points
	 * @param x0 Start X coordinate
	 * @param y0 Start Y coordinate
	 * @param z0 Start Z coordinate
	 * @param x1 End X coordinate
	 * @param y1 End Y coordinate
	 * @param z1 End Z coordinate
     * @return
     */
	public static double distanceOf(double x0, double y0, double z0, double x1, double y1, double z1){
		return Math.sqrt(Math.pow(x0-x1, 2) +
				  	     Math.pow(y0-y1, 2) +
				  	     Math.pow(z0-z1, 2));
	}
}
