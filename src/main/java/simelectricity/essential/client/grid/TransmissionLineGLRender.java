package simelectricity.essential.client.grid;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.math.MathAssitant;
import rikka.librikka.math.Vec3f;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import rikka.librikka.model.quadbuilder.RawQuadGroup;

import org.lwjgl.opengl.GL11;
import simelectricity.common.ConfigManager;

@SideOnly(Side.CLIENT)
public class TransmissionLineGLRender {	
	public static RawQuadGroup renderParabolicCable(Vec3f from, Vec3f to, boolean half, float tension, float thickness, TextureAtlasSprite texture) {
		RawQuadGroup ret = new RawQuadGroup();
		
		float steps = ConfigManager.parabolaRenderSteps;
		float length = from.distanceTo(to);
		float b = 4F * tension / length;
		float a = -b / length;
        float unitLength = length / steps;

        float x0, y0, x1, y1;

        for (int i = 0; i < steps / (half ? 2 : 1); i++) {
            x0 = i * unitLength;
            y0 = x0 * x0 * a + x0 * b;
            x1 = (i + 1) * unitLength;
            y1 = x1 * x1 * a + x1 * b;
            
            ret.add((new RawQuadCube(thickness, MathHelper.sqrt(unitLength*unitLength + (y1 - y0)*(y1 - y0)), thickness, texture))
            			.rotateAroundZ((float) Math.atan2(y0 - y1, unitLength) * 180F / MathAssitant.PI)
            			.translateCoord(y0, i * unitLength, 0)
            			);
        }
        
        ret.rotateToVec(from.x, from.y, from.z, to.x, to.y, to.z);
        
        return ret;
	}
	
    /**
     * Render parabolic cable between two points
     * </p> coordinates are MineCraft coordinates, the cable will start from current openGL reference point
     *
     * @param thickness    
     * @param textureIndex The index of texture
     */
    public static void renderParabolicCable(Vec3f from, Vec3f to, boolean half, float thickness, float tension, TransmissionLineGLRender.ITextureProvider textureProvider, int textureIndex) {
        GL11.glPushMatrix();
        GL11.glTranslatef(from.x, from.y, from.z);
        float distance = from.distanceTo(to);
        p2pRotation(from.x, from.y, from.z, to.x, to.y, to.z, distance);
        render_parabola(distance, half, tension, ConfigManager.parabolaRenderSteps, thickness, textureProvider, textureIndex);
        GL11.glPopMatrix();
    }

    private static void startDrawingQuads() {
        Tessellator.getInstance().getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX);
    }

    private static void addVertexWithUV(double x, double y, double z, double u, double v) {
        Tessellator.getInstance().getBuffer().pos(x, y, z).tex(u, v).endVertex();
    }

    /**
     * Render a cube with given size and texture
     *
     * @param maxX Size of x
     * @param maxY Size of y
     * @param maxZ Size of z
     */
    private static void renderCube(double maxX, double maxY, double maxZ, TransmissionLineGLRender.ITextureProvider textureProvider, int textureIndex) {
        Tessellator t = Tessellator.getInstance();
        GL11.glPushMatrix();

        textureProvider.bindTexture(textureIndex, 4);
        startDrawingQuads();
        addVertexWithUV(-maxX / 2, 0, -maxZ / 2, 0, 1);
        addVertexWithUV(-maxX / 2, 0, maxZ / 2, 1, 1);
        addVertexWithUV(-maxX / 2, maxY, maxZ / 2, 1, 0);
        addVertexWithUV(-maxX / 2, maxY, -maxZ / 2, 0, 0);
        t.draw();

        textureProvider.bindTexture(textureIndex, 5);
        startDrawingQuads();
        addVertexWithUV(maxX / 2, maxY, -maxZ / 2, 1, 0);
        addVertexWithUV(maxX / 2, maxY, maxZ / 2, 0, 0);
        addVertexWithUV(maxX / 2, 0, maxZ / 2, 0, 1);
        addVertexWithUV(maxX / 2, 0, -maxZ / 2, 1, 1);
        t.draw();

        textureProvider.bindTexture(textureIndex, 2);
        startDrawingQuads();
        addVertexWithUV(-maxX / 2, maxY, -maxZ / 2, 1, 0);
        addVertexWithUV(maxX / 2, maxY, -maxZ / 2, 0, 0);
        addVertexWithUV(maxX / 2, 0, -maxZ / 2, 0, 1);
        addVertexWithUV(-maxX / 2, 0, -maxZ / 2, 1, 1);
        t.draw();

        textureProvider.bindTexture(textureIndex, 3);
        startDrawingQuads();
        addVertexWithUV(-maxX / 2, maxY, maxZ / 2, 0, 0);
        addVertexWithUV(-maxX / 2, 0, maxZ / 2, 0, 1);
        addVertexWithUV(maxX / 2, 0, maxZ / 2, 1, 1);
        addVertexWithUV(maxX / 2, maxY, maxZ / 2, 1, 0);
        t.draw();

        textureProvider.bindTexture(textureIndex, 1);
        startDrawingQuads();
        addVertexWithUV(-maxX / 2, maxY, maxZ / 2, 0, 0);
        addVertexWithUV(maxX / 2, maxY, maxZ / 2, 1, 0);
        addVertexWithUV(maxX / 2, maxY, -maxZ / 2, 1, 1);
        addVertexWithUV(-maxX / 2, maxY, -maxZ / 2, 0, 1);
        t.draw();

        textureProvider.bindTexture(textureIndex, 0);
        startDrawingQuads();
        addVertexWithUV(-maxX / 2, 0, -maxZ / 2, 0, 1);
        addVertexWithUV(maxX / 2, 0, -maxZ / 2, 1, 1);
        addVertexWithUV(maxX / 2, 0, maxZ / 2, 1, 0);
        addVertexWithUV(-maxX / 2, 0, maxZ / 2, 0, 0);
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

    private static void render_parabola(double length, boolean half, double maxDisplacement, double steps, double thickness, TransmissionLineGLRender.ITextureProvider textureProvider, int textureIndex) {
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
            TransmissionLineGLRender.renderCube(thickness, Math.sqrt(unitLength * unitLength + Math.pow(y1 - y0, 2)), thickness, textureProvider, textureIndex);
            GL11.glPopMatrix();
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
    private static void p2pRotation(float xStart, float yStart, float zStart, float xEnd, float yEnd, float zEnd, float distance) {
        GL11.glRotated(Math.acos((yEnd - yStart) / distance) * 180 / Math.PI, (zEnd - zStart) / distance, 0, (xStart - xEnd) / distance);
        GL11.glRotated(Math.atan2(zStart - zEnd, xEnd - xStart) * 180 / Math.PI, 0, 1, 0);
    }

    /**
     * Provides texture for custom render
     *
     * @author rikka0w0
     */
    @SideOnly(Side.CLIENT)
    public interface ITextureProvider {
        /**
         * Do bindTexture(ResourceLocation) here!
         *
         * @param index Which cube is being rendered
         * @param side  The side of the cube
         */
        void bindTexture(int index, int side);
    }
}
