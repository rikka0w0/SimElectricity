package simelectricity.essential.client.grid;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import rikka.librikka.math.MathAssitant;
import rikka.librikka.math.Vec3f;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.client.ResourcePaths;
import simelectricity.essential.client.grid.PowerPoleRenderHelper.ConnectionInfo;

/**
 * Cable model helpers should be called during and after ModelBakeEvent
 * @author Rikka0w0
 * @param <T>
 */
@OnlyIn(Dist.CLIENT)
public class PowerPoleBER<T extends BlockEntity & ISEPowerPole> implements BlockEntityRenderer<T> {
    public PowerPoleBER(BlockEntityRendererProvider.Context context) {

	}

    private static TextureAtlasSprite textureCable = null;

	public static void onModelBakeEvent() {
		textureCable = EasyTextureLoader.blockTextureGetter().apply(ResourceLocation.parse(ResourcePaths.hv_cable));
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

	@Override
	public void render(T tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		PowerPoleRenderHelper helper = tileEntity.getRenderHelper();
		if (helper == null)
			return;

		if (helper.needBake()) {
			this.bake(tileEntity, helper);
		}

		RenderType renderType = RenderType.solid();
		VertexConsumer vertexConsumer = buffer.getBuffer(renderType);
		renderQuadGroup(helper.quadBuffer, matrixStack, vertexConsumer, combinedLightIn);
	}

	public static void renderQuadGroup(List<BakedQuad> quads, PoseStack matrixStack, VertexConsumer buffer, int light) {
		if (quads == null)
			return;

		for (BakedQuad quad : quads) {
			int i = light;
			if (!quad.isShade()) {
				i = 15728880;
			}

			matrixStack.pushPose();
			buffer.putBulkData(matrixStack.last(), quad, 1.0F, 1.0F, 1.0F, 1.0F, i, OverlayTexture.NO_OVERLAY);
			matrixStack.popPose();
		}
	}

	@Override
	public AABB getRenderBoundingBox(T blockEntity) {
		return AABB.INFINITE;
	}

	protected void bake(T te, PowerPoleRenderHelper helper) {
		for (PowerPoleRenderHelper.ConnectionInfo[] connections : helper.connectionList) {
			for (PowerPoleRenderHelper.ConnectionInfo connection : connections) {
				if (connection == null)
					continue;

				RawQuadGroup cable = renderParabolicCable(connection.fixedFrom, connection.fixedTo, connection.isVirtual, connection.tension, 0.05F);
				if (cable != null) {
					cable.translateCoord(-helper.pos.getX(), -helper.pos.getY(), -helper.pos.getZ()).bake(helper.quadBuffer);
				}
			}
		}

		for (PowerPoleRenderHelper.ExtraWireInfo extraWire : helper.extraWireList) {
			RawQuadGroup cable = extraWire.useCatenary ?
					renderCatenaryCable(extraWire.from, extraWire.to, false, extraWire.tension, 0.05F) :
					renderParabolicCable(extraWire.from, extraWire.to, false, extraWire.tension, 0.05F);
			if (cable != null) {
				cable.translateCoord(-helper.pos.getX(), -helper.pos.getY(), -helper.pos.getZ()).bake(helper.quadBuffer);
			}
		}
	}

	public static void renderInsulator(PowerPoleRenderHelper helper, RawQuadGroup insulator) {
		if (insulator == null)
			return;

		for (PowerPoleRenderHelper.ConnectionInfo[] connections : helper.connectionList) {
			for (PowerPoleRenderHelper.ConnectionInfo connection : connections) {
				if (connection == null)
					continue;

				insulator.clone()
						.rotateToVec(connection.from, connection.to)
						.translateCoord(connection.from.x - helper.pos.getX(), connection.from.y - helper.pos.getY(), connection.from.z - helper.pos.getZ())
						.bake(helper.quadBuffer);
			}
		}
	}
}
