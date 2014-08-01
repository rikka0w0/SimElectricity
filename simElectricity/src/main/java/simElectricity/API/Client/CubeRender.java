package simElectricity.API.Client;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

/**
 * Provides a easy way of rendering cubes and other shapes made of cubes </p>
 * e.g. parabolas</p>
 * @author rikka0w0
 *
 */
public class CubeRender {
	private ITextureProvider textureProvider;

	public CubeRender(ITextureProvider textureProvider){
		this.textureProvider = textureProvider;
	}

	/**
     * Render a cube, from current normal, with a given size and default texture(index 0)
	 * @param maxX Size of x
	 * @param maxY Size of y
	 * @param maxZ Size of z
	 */
    public void render_cube(double maxX, double maxY, double maxZ) {
    	render_cube(maxX, maxY, maxZ, 0);
    }
	
    /**
     * Render a cube, from current normal, with a given size and texture
	 * @param maxX Size of x
	 * @param maxY Size of y
	 * @param maxZ Size of z
     */
    public void render_cube(double maxX, double maxY, double maxZ, int textureIndex) {
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
     * @param length The distance between two intersections with x-axis
     * @param half If set to true, only half of the curve will be rendered (will stop at maximum y displacement)
     * @param maxDisplacement Maximum y displacement
     * @param steps How many intervals does the parabola has, the larger this value is, the more smooth the parabola will be
     * @param thickness The thickness of the curve
     * @param textureIndex The texture binding to this curve
     */
	public void render_parabola(double length, boolean half, double maxDisplacement, double steps, double thickness, int textureIndex){
		double b = 4 * maxDisplacement / length;
		double a = -b / length;
		double unitLength = length / steps;
		
		double x0, y0, x1, y1;
		
		for (int i = 0; i < steps / (half ? 2 : 1); i++){
			x0 = i * unitLength;
			y0 = x0 * x0 * a + x0 * b;
			x1 = (i + 1) * unitLength;
			y1 = x1 * x1 * a + x1 * b;			
			
			GL11.glPushMatrix(); 
			GL11.glTranslated(y0, i*unitLength, 0);
			GL11.glRotated(Math.toDegrees(Math.atan2(y0-y1, unitLength)), 0, 0, 1);
			render_cube(thickness, Math.sqrt(unitLength * unitLength + Math.pow(y1 - y0, 2)) ,thickness,textureIndex);
			GL11.glPopMatrix();
		}
	}
}
