package simElectricity.Client.Render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simElectricity.API.Client.ITextureProvider;
import simElectricity.Common.ConfigManager;

@SideOnly(Side.CLIENT)
public class CableRender extends CubeRender {
    public CableRender(ITextureProvider textureProvider) {
        super(textureProvider);
    }

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
    public void renderParabolicCable(double xStart, double yStart, double zStart, double xEnd, double yEnd, double zEnd, double thickness, double tension, int textureIndex) {
        double distance = RenderUtil.distanceOf(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        RenderUtil.p2pRotation(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        render_parabola(distance, false, tension, ConfigManager.parabolaRenderSteps, thickness, textureIndex);
    }

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
    public void renderHalfParabolicCable(double xStart, double yStart, double zStart, double xEnd, double yEnd, double zEnd, double thickness, double tension, int textureIndex) {
        double distance = RenderUtil.distanceOf(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        RenderUtil.p2pRotation(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        render_parabola(distance, true, tension, ConfigManager.parabolaRenderSteps, thickness, textureIndex);
    }

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
    public void renderCable(double xStart, double yStart, double zStart, double xEnd, double yEnd, double zEnd, double thickness, int textureIndex) {
        double distance = RenderUtil.distanceOf(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        RenderUtil.p2pRotation(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        render_cube(thickness, distance, thickness, textureIndex);
    }

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
    public void renderHalfCable(double xStart, double yStart, double zStart, double xEnd, double yEnd, double zEnd, double thickness, int textureIndex) {
        double distance = RenderUtil.distanceOf(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        RenderUtil.p2pRotation(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        render_cube(thickness, distance / 2, thickness, textureIndex);
    }
}
