package simelectricity.essential.client.grid.transformer;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.animation.TileEntityRendererFast;
import net.minecraftforge.client.model.pipeline.LightUtil;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.quadbuilder.MutableQuad;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.Essential;
import simelectricity.essential.client.ResourcePaths;
import simelectricity.essential.client.grid.pole.Models;
import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder;

@OnlyIn(Dist.CLIENT)
public class PowerTransformerTER extends TileEntityRendererFast<TilePowerTransformerPlaceHolder.Render> {
    public final static ResourceLocation modelResLoc = new ResourceLocation(Essential.MODID, "block/powertransformer");
    private final static List<BakedQuad> quads = new LinkedList<>();
//    private final static Matrix4f refXMatrix = new Matrix4f(new float[] {
//    		1, 0, 0, 0,
//    		0, 1, 0, 0,
//    		0, 0,-1, 0,
//    		0, 0, 0, 1
//    });

	public PowerTransformerTER(TileEntityRendererDispatcher rendererDispatcherIn) {
		super();
	}

    public static void onModelRegistryEvent() {
    	ModelLoader.addSpecialModel(modelResLoc);
    }
    
    public static void onPreTextureStitchEvent(TextureStitchEvent.Pre event) {
    	if (EasyTextureLoader.isBlockAtlas(event)) {
    		event.addSprite(new ResourceLocation(ResourcePaths.metal));
    		event.addSprite(new ResourceLocation(ResourcePaths.glass_insulator));
    	}
    }
    
    @SuppressWarnings("deprecation")
	public static void onModelBakeEvent() {
		IBakedModel mdl = Minecraft.getInstance().getModelManager().getModel(modelResLoc);
		
		quads.clear();
        quads.addAll(mdl.getQuads(null, null, null));
        for (Direction side: Direction.values()) {
        	quads.addAll(mdl.getQuads(null, side, null));
		}
        
        // HV and LV Bushing
        TextureAtlasSprite textureMetal = EasyTextureLoader.blockTextureGetter().apply(new ResourceLocation(ResourcePaths.metal));
        TextureAtlasSprite textureInsulator = EasyTextureLoader.blockTextureGetter().apply(new ResourceLocation(ResourcePaths.glass_insulator));

        RawQuadGroup model = new RawQuadGroup();
        RawQuadGroup insulator = Models.renderInsulatorString(1.4F, textureInsulator);
        insulator.add((new RawQuadCube(0.1F, 1.8F, 0.1F, textureMetal)).translateCoord(0, -0.1F, 0));
        insulator.translateCoord(0, 0.1F, 0);
        model.merge(insulator.clone().translateCoord(1, 1, -1.5F));
        model.merge(insulator.clone().translateCoord(1, 1, 0));
        model.merge(insulator.translateCoord(1, 1, 1.5F));

        insulator = Models.renderInsulatorString(0.7F, textureInsulator);
        insulator.add((new RawQuadCube(0.1F, 1.1F, 0.1F, textureMetal)).translateCoord(0, -0.1F, 0));
        insulator.translateCoord(0, 0.1F, 0);
        model.merge(insulator.clone().translateCoord(-1, 1, 0.2F));
        model.merge(insulator.clone().translateCoord(-1, 1, 1));
        model.merge(insulator.translateCoord(-1, 1, 1.8F));
        
        model.translateCoord(0.5F, 0, 0.5F).bake(quads);
	}

	@Override
	public void renderTileEntityFast(TilePowerTransformerPlaceHolder.Render te, 
			double x, double y, double z, float partialTicks, int destroyStage,
			BufferBuilder buffer) {
		BlockPos pos = te.getPos();
		Direction facing = te.getFacing();
		boolean mirrored = te.isMirrored();
		if (facing == null)
			return;
		int rotation = (3-facing.getHorizontalIndex())*90;
		//S   W   N   E
		//270 180 90  0
		//0   1   2   3
		
		buffer.setTranslation(x-pos.getX(), y-pos.getY(), z-pos.getZ());

    	//TODO: Fix light calculation
        int i = 15728640;
		for (BakedQuad quad: quads) {
			MutableQuad mquad = new MutableQuad(quad);

			mquad.translateCoord(-0.5f, 0, -0.5f);
			if (mirrored) {
				mquad.vertex_0.position_z = -mquad.vertex_0.position_z;
				mquad.vertex_1.position_z = -mquad.vertex_1.position_z;
				mquad.vertex_2.position_z = -mquad.vertex_2.position_z;
				mquad.vertex_3.position_z = -mquad.vertex_3.position_z;
			}
			mquad.rotateAroundY(rotation);
			mquad.translateCoord(0.5f, 0, 0.5f);
			quad = mquad.bake();
			
			buffer.addVertexData(quad.getVertexData());
			buffer.putBrightness4(i, i, i, i);
			
            float diffuse = LightUtil.diffuseLight(mirrored ? quad.getFace() : quad.getFace().getOpposite());
            
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 4);
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 3);
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 2);
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 1);
			buffer.putPosition(pos.getX(), pos.getY(), pos.getZ());
		}
	}
}
