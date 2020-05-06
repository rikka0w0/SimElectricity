package simelectricity.essential.client.grid.transformer;

import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
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
import rikka.librikka.model.quadbuilder.RawQuadCube;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.Essential;
import simelectricity.essential.client.ResourcePaths;
import simelectricity.essential.client.grid.pole.Models;
import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder;

@OnlyIn(Dist.CLIENT)
public class FastTESRPowerTransformer extends TileEntityRenderer<TilePowerTransformerPlaceHolder.Render>{
    public final static ResourceLocation modelResLoc = new ResourceLocation(Essential.MODID, "block/powertransformer");
    private final static List<BakedQuad> quads = new LinkedList<>();
    private final static Matrix4f refXMatrix = new Matrix4f(new float[] {
    		1, 0, 0, 0,
    		0, 1, 0, 0,
    		0, 0,-1, 0,
    		0, 0, 0, 1
    });

    public FastTESRPowerTransformer(TileEntityRendererDispatcher rendererDispatcherIn) {
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
        
        LinkedList<BakedQuad> quads2 = new LinkedList<>();
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
	public void render(TilePowerTransformerPlaceHolder.Render te, float partialTicks, MatrixStack matrixStack,
			IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		Direction facing = te.getFacing();
		boolean mirrored = te.isMirrored();
		if (facing == null)
			return;
		int rotation = (3-facing.getHorizontalIndex())*90;
		//S   W   N   E
		//270 180 90  0
		//0   1   2   3
		
        IVertexBuilder buffer = bufferIn.getBuffer(RenderType.getSolid());
        
    	//TODO: Fix light calculation
        int lightDummy = 15728640;
		for (BakedQuad quad: quads) {
			matrixStack.push();
			
			matrixStack.translate(0.5, 0, 0.5);
			matrixStack.rotate(Vector3f.YP.rotationDegrees(rotation));
			if (mirrored)
				matrixStack.getLast().getMatrix().mul(refXMatrix);
			matrixStack.translate(-0.5, 0, -0.5);
			
			buffer.addQuad(matrixStack.getLast(), quad, 
					new float[]{1.0F, 1.0F, 1.0F, 1.0F}, 
					1.0F, 1.0F, 1.0F, 
					new int[]{lightDummy, lightDummy, lightDummy, lightDummy}, 
					OverlayTexture.NO_OVERLAY, true);
			
			matrixStack.pop();
		}
	}
}
