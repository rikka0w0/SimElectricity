package simelectricity.essential.client.grid.pole;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import rikka.librikka.DirHorizontal8;
import simelectricity.essential.grid.TilePoleMetal35kV;

public class MetalPole35kVBottomTER implements BlockEntityRenderer<TilePoleMetal35kV.Bottom>{
	public MetalPole35kVBottomTER(BlockEntityRendererProvider.Context context) {

	}

	@Override
	public void render(TilePoleMetal35kV.Bottom te, float partialTicks, PoseStack matrixStack,
			MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		DirHorizontal8 facing8 = te.facing();
		int facing = (8-facing8.ordinal()) & 7;
		int part = te.getPartId();

		BlockState blockState = te.getBlockState();
		if (blockState == null)
			return;
		BakedModel bakedmodel = Minecraft.getInstance().getModelManager()
				.getBlockModelShaper().getBlockModel(blockState);
		if (!(bakedmodel instanceof MetalPole35kVModel))
			return;
		MetalPole35kVModel model = (MetalPole35kVModel) bakedmodel;

        VertexConsumer buffer = bufferIn.getBuffer(RenderType.solid());

        int i = 15728640;
		for (BakedQuad quad: model.bakedModelBasePart[facing][part]) {
			matrixStack.pushPose();
			buffer.putBulkData(matrixStack.last(), quad, 1, 1, 1, i, OverlayTexture.NO_OVERLAY);
			matrixStack.popPose();
		}
	}

}
