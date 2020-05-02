package simelectricity.essential;

import java.beans.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.MinecraftForgeClient;
import simelectricity.essential.TESRTestBlock.Tile;

public class TESR extends TileEntityRenderer<TESRTestBlock.Tile>{
	public TESR(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(Tile te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer,
			int combinedLight, int combinedOverlay) {

		IVertexBuilder builder = buffer.getBuffer(RenderType.getCutout());
        BlockState state = te.getWorld().getBlockState(te.getPos().down());
        IBakedModel model = Minecraft.getInstance().getModelManager().getModel(BlockModelShapes.getModelLocation(state));
		
       
//		matrixStack.push();
//		matrixStack.translate(0, 2, 0);
//        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(matrixStack.getLast(), builder, state, model, r, g, b, 15728784, OverlayTexture.NO_OVERLAY);
//		matrixStack.pop();
//		
//		if (true)
//			return;
		
		


        Random rand = new Random();
		List<BakedQuad> quads = new LinkedList<>();
		for (Direction side: Direction.values()) {
			rand.setSeed(42L);
			quads.addAll(model.getQuads(state, side, rand));
		}
		
		matrixStack.push();
		matrixStack.translate(0, 0, 0);
		for (BakedQuad quad: quads) {
		      float f;
		      float f1;
		      float f2;
		      if (quad.hasTintIndex()) {
		         int i = Minecraft.getInstance().getBlockColors().getColor(state, te.getWorld(), te.getPos(), quad.getTintIndex());
		         f = (float)(i >> 16 & 255) / 255.0F;
		         f1 = (float)(i >> 8 & 255) / 255.0F;
		         f2 = (float)(i & 255) / 255.0F;
		      } else {
		         f = 1.0F;
		         f1 = 1.0F;
		         f2 = 1.0F;
		      }
		      // FORGE: Apply diffuse lighting at render-time instead of baking it in
		      if (quad.shouldApplyDiffuseLighting()) {
		         // TODO this should be handled by the forge lighting pipeline
		         float l = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(quad.getFace());
		         f *= l;
		         f1 *= l;
		         f2 *= l;
		      }
		      int i = 15728640;
		      int j = WorldRenderer.getPackedLightmapCoords(te.getWorld(), state, te.getPos());
			builder.addQuad(matrixStack.getLast(), quad, f, f1, f2, 15728640, OverlayTexture.NO_OVERLAY);
//		    builder.addQuad(matrixStack.getLast(), quad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, f, f1, f2, new int[]{i, i, i, i}, 655360, true);
		}
		matrixStack.pop();
	}

}
