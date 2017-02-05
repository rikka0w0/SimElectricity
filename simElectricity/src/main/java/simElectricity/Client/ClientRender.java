package simElectricity.Client;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import simElectricity.DummyClientRender;
import simElectricity.SimElectricity;
import simElectricity.API.SEAPI;
import simElectricity.API.Client.ITextureProvider;
import simElectricity.API.Internal.IClientRender;
import simElectricity.Common.ConfigManager;

@SideOnly(Side.CLIENT)
public class ClientRender extends DummyClientRender implements IClientRender{
	public static void initClientAPI(){
		ClientRender instance = new ClientRender();
		SEAPI.clientRender = instance;
		SimElectricity.clientWorldHandler = instance;
	}
	
	public World getClientWorld(){
		return FMLClientHandler.instance().getClient().theWorld;
	}
	
    public void renderParabolicCable(double xStart, double yStart, double zStart, double xEnd, double yEnd, double zEnd, double thickness, double tension, ITextureProvider textureProvider, int textureIndex) {
        double distance = distanceOf(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        p2pRotation(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        render_parabola(distance, false, tension, ConfigManager.parabolaRenderSteps, thickness, textureProvider, textureIndex);
    }

    public void renderHalfParabolicCable(double xStart, double yStart, double zStart, double xEnd, double yEnd, double zEnd, double thickness, double tension, ITextureProvider textureProvider, int textureIndex) {
        double distance = distanceOf(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        p2pRotation(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        render_parabola(distance, true, tension, ConfigManager.parabolaRenderSteps, thickness, textureProvider, textureIndex);
    }

    public void renderCable(double xStart, double yStart, double zStart, double xEnd, double yEnd, double zEnd, double thickness, ITextureProvider textureProvider, int textureIndex) {
        double distance = distanceOf(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        p2pRotation(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        renderCube(thickness, distance, thickness, textureProvider, textureIndex);
    }

    public void renderHalfCable(double xStart, double yStart, double zStart, double xEnd, double yEnd, double zEnd, double thickness, ITextureProvider textureProvider, int textureIndex) {
        double distance = distanceOf(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        p2pRotation(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        renderCube(thickness, distance / 2, thickness, textureProvider, textureIndex);
    }
    
    public void renderCube(double maxX, double maxY, double maxZ, ITextureProvider textureProvider, int textureIndex) {
        Tessellator t = Tessellator.instance;

        GL11.glPushMatrix();

        textureProvider.bindTexture(textureIndex, 4);
        t.startDrawingQuads();
        t.setNormal(-1, 0, 0);
        t.addVertexWithUV(-maxX / 2, 0, -maxZ / 2, 0, 1);
        t.addVertexWithUV(-maxX / 2, 0, maxZ / 2, 1, 1);
        t.addVertexWithUV(-maxX / 2, maxY, maxZ / 2, 1, 0);
        t.addVertexWithUV(-maxX / 2, maxY, -maxZ / 2, 0, 0);
        t.draw();

        textureProvider.bindTexture(textureIndex, 5);
        t.startDrawingQuads();
        t.setNormal(1, 0, 0);
        t.addVertexWithUV(maxX / 2, maxY, -maxZ / 2, 1, 0);
        t.addVertexWithUV(maxX / 2, maxY, maxZ / 2, 0, 0);
        t.addVertexWithUV(maxX / 2, 0, maxZ / 2, 0, 1);
        t.addVertexWithUV(maxX / 2, 0, -maxZ / 2, 1, 1);
        t.draw();

        textureProvider.bindTexture(textureIndex, 2);
        t.startDrawingQuads();
        t.setNormal(0, 0, -1);
        t.addVertexWithUV(-maxX / 2, maxY, -maxZ / 2, 1, 0);
        t.addVertexWithUV(maxX / 2, maxY, -maxZ / 2, 0, 0);
        t.addVertexWithUV(maxX / 2, 0, -maxZ / 2, 0, 1);
        t.addVertexWithUV(-maxX / 2, 0, -maxZ / 2, 1, 1);
        t.draw();

        textureProvider.bindTexture(textureIndex, 3);
        t.startDrawingQuads();
        t.setNormal(0, 0, 1);
        t.addVertexWithUV(-maxX / 2, maxY, maxZ / 2, 0, 0);
        t.addVertexWithUV(-maxX / 2, 0, maxZ / 2, 0, 1);
        t.addVertexWithUV(maxX / 2, 0, maxZ / 2, 1, 1);
        t.addVertexWithUV(maxX / 2, maxY, maxZ / 2, 1, 0);
        t.draw();

        textureProvider.bindTexture(textureIndex, 1);
        t.startDrawingQuads();
        t.setNormal(0, 1, 0);
        t.addVertexWithUV(-maxX / 2, maxY, maxZ / 2, 0, 0);
        t.addVertexWithUV(maxX / 2, maxY, maxZ / 2, 1, 0);
        t.addVertexWithUV(maxX / 2, maxY, -maxZ / 2, 1, 1);
        t.addVertexWithUV(-maxX / 2, maxY, -maxZ / 2, 0, 1);
        t.draw();

        textureProvider.bindTexture(textureIndex, 0);
        t.startDrawingQuads();
        t.setNormal(0, -1, 0);
        t.addVertexWithUV(-maxX / 2, 0, -maxZ / 2, 0, 1);
        t.addVertexWithUV(maxX / 2, 0, -maxZ / 2, 1, 1);
        t.addVertexWithUV(maxX / 2, 0, maxZ / 2, 1, 0);
        t.addVertexWithUV(-maxX / 2, 0, maxZ / 2, 0, 0);
        t.draw();

        GL11.glPopMatrix();
    }

    /**
     * Render a parabola (made of intervals)
     *
     * @param length          The distance between two intersections with x-axis
     * @param half            If set to true, only half of the curve will be rendered (will stop at maximum y displacement)
     * @param maxDisplacement Maximum y displacement
     * @param steps           How many intervals does the parabola has, the larger this value is, the more smooth the parabola will be
     * @param thickness       The thickness of the curve
     * @param textureIndex    The texture binding to this curve
     */
    private void render_parabola(double length, boolean half, double maxDisplacement, double steps, double thickness,ITextureProvider textureProvider, int textureIndex) {
        double b = 4 * maxDisplacement / length;
        double a = -b / length;
        double unitLength = length / steps;

        double x0, y0, x1, y1;

        for (int i = 0; i < steps / (half ? 2 : 1); i++) {
            x0 = i * unitLength;
            y0 = x0 * x0 * a + x0 * b;
            x1 = (i + 1) * unitLength;
            y1 = x1 * x1 * a + x1 * b;

            GL11.glPushMatrix();
            GL11.glTranslated(y0, i * unitLength, 0);
            GL11.glRotated(Math.toDegrees(Math.atan2(y0 - y1, unitLength)), 0, 0, 1);
            renderCube(thickness, Math.sqrt(unitLength * unitLength + Math.pow(y1 - y0, 2)), thickness,textureProvider,  textureIndex);
            GL11.glPopMatrix();
        }
    }
    
    public int getDirection(int facing) {
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
    public void p2pRotation(double xStart, double yStart, double zStart, double xEnd, double yEnd, double zEnd) {
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
    public double distanceOf(double xStart, double yStart, double zStart, double xEnd, double yEnd, double zEnd) {
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
    public double distanceOf(double xStart, double zStart, double xEnd, double zEnd) {
        return Math.sqrt(Math.pow(xStart - xEnd, 2) +
                Math.pow(zStart - zEnd, 2));
    }
}
