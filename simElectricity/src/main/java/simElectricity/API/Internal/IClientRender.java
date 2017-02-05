package simElectricity.API.Internal;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simElectricity.API.Client.ITextureProvider;

/**
 * Provides a easy way of rendering cubes and wires
 * <p/>
 *
 * @author rikka0w0
 */
@SideOnly(Side.CLIENT)
public interface IClientRender {
    /**
     * Render a parabolic wire between two points
     *
     * @param xStart       Start X coordinate
     * @param yStart       Start Y coordinate
     * @param zStart       Start Z coordinate
     * @param xEnd         End X coordinate
     * @param yEnd         End Y coordinate
     * @param zEnd         End Z coordinate
     * @param thickness    Thickness of the cable
     * @param textureIndex The index of texture
     */
	public void renderParabolicCable(
			double xStart, double yStart, double zStart,
			double xEnd, double yEnd, double zEnd,
			double thickness, double tension, ITextureProvider textureProvider, int textureIndex);
	
    /**
     * Render half of a parabolic wire between two points
     *
     * @param xStart       Start X coordinate
     * @param yStart       Start Y coordinate
     * @param zStart       Start Z coordinate
     * @param xEnd         End X coordinate
     * @param yEnd         End Y coordinate
     * @param zEnd         End Z coordinate
     * @param thickness    Thickness of the cable
     * @param textureIndex The index of texture
     */
	public void renderHalfParabolicCable(
			double xStart, double yStart, double zStart,
			double xEnd, double yEnd, double zEnd,
			double thickness, double tension, ITextureProvider textureProvider, int textureIndex);
	
    /**
     * Render a straight piece of wire between two points
     *
     * @param xStart       Start X coordinate
     * @param yStart       Start Y coordinate
     * @param zStart       Start Z coordinate
     * @param xEnd         End X coordinate
     * @param yEnd         End Y coordinate
     * @param zEnd         End Z coordinate
     * @param thickness    Thickness of the cable
     * @param textureIndex The index of texture
     */
	public void renderCable(
			double xStart, double yStart, double zStart,
			double xEnd, double yEnd, double zEnd,
			double thickness, ITextureProvider textureProvider, int textureIndex);
	
    /**
     * Render half of a straight piece of wire between two points
     *
     * @param xStart       Start X coordinate
     * @param yStart       Start Y coordinate
     * @param zStart       Start Z coordinate
     * @param xEnd         End X coordinate
     * @param yEnd         End Y coordinate
     * @param zEnd         End Z coordinate
     * @param thickness    Thickness of the cable
     * @param textureIndex The index of texture
     */
	public void renderHalfCable(
			double xStart, double yStart, double zStart,
			double xEnd, double yEnd, double zEnd,
			double thickness, ITextureProvider textureProvider, int textureIndex);
	
    /**
     * Render a cube, from current normal, with a given size and texture
     *
     * @param maxX Size of x
     * @param maxY Size of y
     * @param maxZ Size of z
     */
	public void renderCube(double maxX, double maxY, double maxZ,
			ITextureProvider textureProvider, int textureIndex);
	
    public int getDirection(int facing);
	
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
    public void p2pRotation(
    		double xStart, double yStart, double zStart,
    		double xEnd, double yEnd, double zEnd);

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
    public double distanceOf(
    		double xStart, double yStart, double zStart,
    		double xEnd, double yEnd, double zEnd);
    
    /**
     * Calculate the distance between two points
     *
     * @param xStart Start X coordinate
     * @param zStart Start Z coordinate
     * @param xEnd   End X coordinate
     * @param zEnd   End Z coordinate
     */
    public double distanceOf(double xStart, double zStart, double xEnd, double zEnd);
}
