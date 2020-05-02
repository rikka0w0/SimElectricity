package simelectricity.essential.client.grid;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;

import rikka.librikka.math.MathAssitant;
import rikka.librikka.math.Vec3f;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.ConfigProvider;
import simelectricity.essential.client.ResourcePaths;
import simelectricity.essential.client.grid.PowerPoleRenderHelper.ConnectionInfo;

@OnlyIn(Dist.CLIENT)
public class FastTESRPowerPole<T extends TileEntity & ISEPowerPole> extends TileEntityRenderer<T> {	
    public FastTESRPowerPole(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	private static FastTESRPowerPole instance;
    public static TextureAtlasSprite texture;
    public final static ResourceLocation hvcable_texture_loc = new ResourceLocation(ResourcePaths.hv_cable);

    /**
     * Do not call this
     */
//    public static void stitchTexture(TextureMap map) {
//        texture = map.registerSprite();
//    }

    public static RawQuadGroup renderParabolicCable(Object[] vertexAndTension, float thickness) {
    	return renderParabolicCable(vertexAndTension, thickness, texture);
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
    
    public static RawQuadGroup renderParabolicCable(Vec3f from, Vec3f to, boolean half, float tension, float thickness) {
    	return renderParabolicCable(from, to, half, tension, thickness, texture);
    }
    
	public static RawQuadGroup renderParabolicCable(Vec3f from, Vec3f to, boolean half, float tension, float thickness, TextureAtlasSprite texture) {
		RawQuadGroup ret = new RawQuadGroup();
		
		float steps = ConfigProvider.parabolaRenderSteps;
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
	
	public static RawQuadGroup renderCatenaryCable(Vec3f from, Vec3f to, boolean half, float tension, float thickness) {
		return renderCatenaryCable(from, to, half, tension, thickness, texture);
	}
	
	public static RawQuadGroup renderCatenaryCable(Vec3f from, Vec3f to, boolean half, float tension, float thickness, TextureAtlasSprite texture) {
		RawQuadGroup ret = new RawQuadGroup();
		
		float steps = ConfigProvider.parabolaRenderSteps;
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
	
    public static void renderInsulator(Vec3i pos, Vec3f from, Vec3f to, float angle, RawQuadGroup modelInsulator, List<BakedQuad> quads) {
    	modelInsulator = modelInsulator.clone();
    	modelInsulator.rotateAroundZ(angle / MathAssitant.PI * 180);
    	modelInsulator.rotateToVec(from.x, from.y, from.z, to.x, from.y, to.z);
    	modelInsulator.translateCoord(from.x-pos.getX(), from.y-pos.getY(), from.z-pos.getZ());
    	modelInsulator.bake(quads);
    }
    
    protected void renderInsulator(PowerPoleRenderHelper helper, RawQuadGroup modelInsulator) {
        for (ConnectionInfo[] connections : helper.connectionList) {
            for (ConnectionInfo connection : connections) {
                renderInsulator(helper.pos, connection.from, connection.fixedTo, connection.insulatorAngle, modelInsulator, helper.quadBuffer);
            }
        }
    }
    
    /////////////////////////
    //// FastTESRPowerPole
    /////////////////////////
    
    @Override
    public boolean isGlobalRenderer(TileEntity te) {
        return true;
    }
    
    protected void bake(T te, PowerPoleRenderHelper helper) {
    	if (helper.extraWireList.isEmpty() && helper.connectionList.isEmpty())
    		return;
    	
		BlockPos pos = helper.pos;  
        for (PowerPoleRenderHelper.ConnectionInfo[] connections : helper.connectionList) {
            for (PowerPoleRenderHelper.ConnectionInfo info : connections) {
            	RawQuadGroup group = renderParabolicCable(info.fixedFrom, info.fixedTo, true, info.tension, 0.06F, texture);
            	group.translateCoord(-pos.getX(), -pos.getY(), -pos.getZ());
            	group.bake(helper.quadBuffer);
            }
        }
        
        for (PowerPoleRenderHelper.ExtraWireInfo wire : helper.extraWireList) {
        	RawQuadGroup group = wire.useCatenary ? 
        			renderCatenaryCable(wire.from, wire.to, false, wire.tension, 0.06F, texture) :
        			renderParabolicCable(wire.from, wire.to, false, wire.tension, 0.06F, texture);
        	group.translateCoord(-pos.getX(), -pos.getY(), -pos.getZ());
        	group.bake(helper.quadBuffer);
        }
    }
    
	@Override
	public void render(T te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn,
			int combinedLightIn, int combinedOverlayIn) {
		if (te.isRemoved())
			return;
		
		PowerPoleRenderHelper helper = te.getRenderHelper();
		
        if (helper == null)
            return;
              
        if (helper.needBake())
        	bake(te, helper);
        
        IVertexBuilder buffer = bufferIn.getBuffer(MinecraftForgeClient.getRenderLayer());
//        buffer.setTranslation(x, y, z);
//        matrix.translate(x, y, z);
		
		int i = 15728640;
		for (BakedQuad quad: helper.quadBuffer) {
//			buffer.addVertexData(quad.getVertexData());
//			buffer.putBrightness4(i, i, i, i);
//			
//			float diffuse = 1;
//            if(quad.shouldApplyDiffuseLighting())
//                diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(quad.getFace());
//
//            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 4);
//            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 3);
//            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 2);
//            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 1);
//			
//			buffer.putPosition(0, 0, 0);
			matrixStack.push();
			buffer.addQuad(matrixStack.getLast(), quad, 1, 1, 1, 15728640, OverlayTexture.NO_OVERLAY);
			matrixStack.pop();
		}
	}
}
