package simelectricity.essential.client.grid;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.MathHelper;
import rikka.librikka.math.MathAssitant;
import rikka.librikka.math.Vec3f;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.client.ClientConfigs;

public class PowerCableBakery {
	public static RawQuadGroup renderParabolicCable(Vec3f from, Vec3f to, boolean half, float tension, float thickness, TextureAtlasSprite texture) {
		RawQuadGroup ret = new RawQuadGroup();
		
		float steps = ClientConfigs.parabolaRenderSteps.get();
		float length = from.distanceTo(to);	
		float b = 4F * tension / length;
		float a = -b / length;
	    float unitLength = length / steps;
	
	    float y0 = 0, y1;
	
	    for (int i = 0; i < steps / (half ? 2 : 1); i++) {
	    	float x1 = (i + 1) * unitLength;
	        y1 = x1 * x1 * a + x1 * b;
	        
	        ret.add((new RawQuadCube(thickness, MathHelper.sqrt(unitLength*unitLength + (y1 - y0)*(y1 - y0)), thickness, texture))
	        			.rotateAroundZ((float) Math.atan2(y0 - y1, unitLength) * 180F / MathAssitant.PI)
	        			.translateCoord(y0, i * unitLength, 0)
	        			);
	        y0 = y1;
	    }
	    
	    ret.rotateToVec(from.x, from.y, from.z, to.x, to.y, to.z);
	    ret.translateCoord(from.x, from.y, from.z);
	    return ret;
	}

	public static RawQuadGroup renderCatenaryCable(Vec3f from, Vec3f to, boolean half, float tension, float thickness, TextureAtlasSprite texture) {
		RawQuadGroup ret = new RawQuadGroup();
		
		float steps = ClientConfigs.parabolaRenderSteps.get();
	    float y0 = 0, y1;
	
	    float d = MathHelper.sqrt((from.x-to.x)*(from.x-to.x) + (from.z-to.z)*(from.z-to.z));
	    float step = d/steps;
	    Catenary c = new Catenary(0, to.y-from.y, d, tension);
	    
	    //Origin
	    for (int i = 0; i < steps / (half ? 2 : 1); i++) {
	        y1 = c.apply((i + 1) * step);
	        
	        ret.add((new RawQuadCube(thickness, MathHelper.sqrt(step*step + (y1 - y0)*(y1 - y0)), thickness, texture))
	        			.rotateAroundZ(-(float) Math.atan2(y0 - y1, step) * 180F / MathAssitant.PI)
	        			.translateCoord(-y0, i * step, 0)
	        			);
	        y0 = y1;
	    }
	    
	    ret.rotateToVec(from.x, 0, from.z, to.x, 0, to.z);
	    ret.translateCoord(from.x, from.y, from.z);
	    return ret;
	}

	/**
	 * 
	 * @param vertexesAndTension Vec3f, float, Vec3f, float, ..., Vec3f
	 * @param thickness
	 * @param texture
	 * @return
	 */
	public static RawQuadGroup renderParabolicCable(Object[] vertexesAndTension, float thickness, TextureAtlasSprite texture) {
		RawQuadGroup ret = new RawQuadGroup();
		Vec3f start = (Vec3f) vertexesAndTension[0];
		for (int i=1; i<vertexesAndTension.length; i+=2) {
			float tension = (float) vertexesAndTension[i];
			Vec3f end = (Vec3f) vertexesAndTension[i+1];
			ret.merge(renderParabolicCable(start, end, false, tension, thickness, texture));
			start = end;
		}
		return ret;
	}

}
