package simelectricity.essential.client.grid.transformer;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.quadbuilder.MutableQuad;
import rikka.librikka.model.quadbuilder.RawQuadCube;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.Essential;
import simelectricity.essential.client.ResourcePaths;
import simelectricity.essential.client.grid.pole.Models;
import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder;

@OnlyIn(Dist.CLIENT)
public class PowerTransformerTER extends TileEntityRenderer<TilePowerTransformerPlaceHolder.Render> {
    public final static ResourceLocation modelResLoc = new ResourceLocation(Essential.MODID, "block/powertransformer");
    @SuppressWarnings("unchecked")
	private final static List<BakedQuad>[] bakedModel = new List[4];
    @SuppressWarnings("unchecked")
	private final static List<BakedQuad>[] bakedModelMirrored = new List[4];

    public PowerTransformerTER(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
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

	public static void onModelBakeEvent() {
		for (int i=0; i<bakedModel.length; i++) {
			bakedModel[i] = null;
			bakedModelMirrored[i] = null;
		}
	}

	@Override
	public void render(TilePowerTransformerPlaceHolder.Render te, float partialTicks, MatrixStack matrixStack,
			IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		Direction facing = te.getFacing();
		boolean mirrored = te.isMirrored();
		if (facing == null)
			return;
		
		//S   W   N   E
		//270 180 90  0
		//0   1   2   3
		
        IVertexBuilder buffer = bufferIn.getBuffer(RenderType.getSolid());
        
    	//TODO: Fix light calculation
        int lightDummy = 15728640;
		for (BakedQuad quad: getQuads(facing, mirrored)) {
			matrixStack.push();

			buffer.addQuad(matrixStack.getLast(), quad, 
					new float[]{1.0F, 1.0F, 1.0F, 1.0F}, 
					1.0F, 1.0F, 1.0F, 
					new int[]{lightDummy, lightDummy, lightDummy, lightDummy}, 
					OverlayTexture.NO_OVERLAY, true);

			matrixStack.pop();
		}
	}


	public static List<BakedQuad> getQuads(Direction facing, boolean mirrored) {
    	List<BakedQuad> quads = mirrored ? bakedModelMirrored[facing.ordinal()-2] : bakedModel[facing.ordinal()-2];
    	if (quads == null) {
    		quads = new LinkedList<>();
    		if (mirrored) {
    			bakedModelMirrored[facing.ordinal()-2] = quads;
    		} else {
    			bakedModel[facing.ordinal()-2] = quads;
    		}
    	}
    	
    	if (quads.isEmpty()) {
    		bake(quads, facing, mirrored);
    	}
    	
    	return quads;
    }

    @SuppressWarnings("deprecation")
    public static void bake(List<BakedQuad> quads, Direction facing, boolean mirrored) {
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
        
        // Rotate and flip the model
        int rotation = (3-facing.getHorizontalIndex())*90;
        List<MutableQuad> mquads = quads.stream().map(MutableQuad::new).collect(Collectors.toList());
        quads.clear();
		for (MutableQuad mquad: mquads) {
			mquad.translateCoord(-0.5f, 0, -0.5f);
			if (mirrored) {
				mquad.vertex_0.position_z = -mquad.vertex_0.position_z;
				mquad.vertex_1.position_z = -mquad.vertex_1.position_z;
				mquad.vertex_2.position_z = -mquad.vertex_2.position_z;
				mquad.vertex_3.position_z = -mquad.vertex_3.position_z;
			}
			mquad.rotateAroundY(rotation);
			mquad.translateCoord(0.5f, 0, 0.5f);
			quads.add(mquad.bake());
		}
    }
}
