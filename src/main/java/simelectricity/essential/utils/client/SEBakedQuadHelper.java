package simelectricity.essential.utils.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.LightUtil;

public class SEBakedQuadHelper {
	public static int[] vertexToInts(float x, float y, float z, int color, TextureAtlasSprite texture, float u, float v)
	{
	    return new int[] {
	            Float.floatToRawIntBits(x),
	            Float.floatToRawIntBits(y),
	            Float.floatToRawIntBits(z),
	            color,
	            Float.floatToRawIntBits(texture.getInterpolatedU(u)),
	            Float.floatToRawIntBits(texture.getInterpolatedV(v)),
	            0
	    };
	}
	
	public static EnumFacing getFacingFromVertexes(
			float x1, float y1, float z1,
			float x2, float y2, float z2,
			float x3, float y3, float z3,
			float x4, float y4, float z4){
		float xa = x1 - x2;
		float ya = y1 - y2;
		float za = z1 - z2;
		float xb = x3 - x4;
		float yb = y3 - y4;
		float zb = z3 - y4;
		
		//CrossProduct
		float xc = ya*zb - za*yb;
		float yc = za*xb - xa*zb;
		float zc = xa*yb - ya*xb;
		
		//{xc, yc, zc} = normal vector
		return LightUtil.toSide(xc, yc, zc);
	}
}
