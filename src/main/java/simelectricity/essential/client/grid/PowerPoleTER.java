package simelectricity.essential.client.grid;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import rikka.librikka.math.MathAssitant;
import rikka.librikka.math.Vec3f;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.client.ResourcePaths;
import simelectricity.essential.client.grid.PowerPoleRenderHelper.ConnectionInfo;

/**
 * Cable model helpers should be called during and after {@link net.minecraftforge.client.event.ModelBakeEvent}
 * @author Rikka0w0
 * @param <T>
 */
@OnlyIn(Dist.CLIENT)
public class PowerPoleTER<T extends TileEntity & ISEPowerPole> extends TileEntityRenderer<T> {	
    public PowerPoleTER(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

    private static TextureAtlasSprite textureCable = null;

    public static void onPreTextureStitchEvent(TextureStitchEvent.Pre event) {
    	if (EasyTextureLoader.isBlockAtlas(event))
    		event.addSprite(new ResourceLocation(ResourcePaths.hv_cable));
    }

	public static void onModelBakeEvent() {
		textureCable = EasyTextureLoader.blockTextureGetter().apply(new ResourceLocation(ResourcePaths.hv_cable));
	}

    public static RawQuadGroup renderParabolicCable(Object[] vertexAndTension, float thickness) {
    	return PowerCableBakery.renderParabolicCable(vertexAndTension, thickness, textureCable);
    }
    
    public static RawQuadGroup renderParabolicCable(Vec3f from, Vec3f to, boolean half, float tension, float thickness) {
    	return PowerCableBakery.renderParabolicCable(from, to, half, tension, thickness, textureCable);
    }
    
	public static RawQuadGroup renderCatenaryCable(Vec3f from, Vec3f to, boolean half, float tension, float thickness) {
		return PowerCableBakery.renderCatenaryCable(from, to, half, tension, thickness, textureCable);
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
            	RawQuadGroup group = PowerCableBakery.renderParabolicCable(info.fixedFrom, info.fixedTo, true, info.tension, 0.06F, textureCable);
            	group.translateCoord(-pos.getX(), -pos.getY(), -pos.getZ());
            	group.bake(helper.quadBuffer);
            }
        }
        
        for (PowerPoleRenderHelper.ExtraWireInfo wire : helper.extraWireList) {
        	RawQuadGroup group = wire.useCatenary ? 
        			PowerCableBakery.renderCatenaryCable(wire.from, wire.to, false, wire.tension, 0.06F, textureCable) :
        			PowerCableBakery.renderParabolicCable(wire.from, wire.to, false, wire.tension, 0.06F, textureCable);
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
        
        IVertexBuilder buffer = bufferIn.getBuffer(RenderType.getSolid());
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
			buffer.addQuad(matrixStack.getLast(), quad, 1, 1, 1, i, OverlayTexture.NO_OVERLAY);
			matrixStack.pop();
		}
	}
}
