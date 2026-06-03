package simelectricity.essential.client.semachine;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import rikka.librikka.model.quadbuilder.MutableQuad;
import simelectricity.essential.api.ISECoverPanelHost;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.api.coverpanel.ISEFacadeCoverPanel;
import simelectricity.essential.client.coverpanel.GenericFacadeRender;
import simelectricity.essential.common.semachine.ISESocketProvider;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.util.RandomSource;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public final class SEMachineModel implements IDynamicBakedModel {
	private final BakedModel bakedModel;

	public SEMachineModel(BakedModel bakedModel) {
		this.bakedModel = bakedModel;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return this.bakedModel.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return this.bakedModel.isGui3d();
	}

	@Override
	public boolean isCustomRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return this.bakedModel.getParticleIcon();
	}

	@Override
	public ItemTransforms getTransforms() {
		return this.bakedModel.getTransforms();
	}

	@Override
	public boolean usesBlockLight() {
		return this.bakedModel.usesBlockLight();
	}

	@Override
	public ItemOverrides getOverrides() {
		return ItemOverrides.EMPTY;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData extraData,
			RenderType renderType) {
		// 【修复 1】: 使用 5 参数方法，传递 extraData
		if (renderType == null)
			return this.bakedModel.getQuads(state, side, rand, extraData, null);

		List<BakedQuad> quads = new LinkedList<>();

		boolean hideMachineFace = false;

		// Render facade cover panel, if possible
		ISECoverPanelHost host = extraData.get(ISECoverPanelHost.prop);
		if (host != null && side != null && renderType != RenderType.solid()) {
			ISECoverPanel coverPanel = host.getCoverPanelOnSide(side);
			if (coverPanel instanceof ISEFacadeCoverPanel) {
				BlockState blockState = ((ISEFacadeCoverPanel) coverPanel).getBlockState();
				BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);

				model.getQuads(blockState, side, rand, ModelData.EMPTY, renderType).stream()
						.map(MutableQuad::new)
						.peek((mquad) -> GenericFacadeRender.tintFunc(side, mquad))
						.map(MutableQuad::bake)
						.forEach(quads::add);

				hideMachineFace = true;
			}
		}

		// 【修复核心】：移除 renderType == RenderType.solid() 的限制
		// 允许底层的 OBJ/Vanilla 模型在它真正需要的渲染层（比如 Cutout）中生成多边形
		if (!hideMachineFace) {
			var innerRenderTypes = this.bakedModel.getRenderTypes(state, rand, extraData);
			if (innerRenderTypes.contains(renderType)) {
				quads.addAll(this.bakedModel.getQuads(state, side, rand, extraData, renderType));
			}
		}

		// Render the sockets in the cutout layer
		if (side == null && renderType == RenderType.cutout()) {
			ISESocketProvider sp = extraData.get(ISESocketProvider.prop);
			if (sp != null)
				SocketRender.getBaked(quads, sp);
		}

		return quads;
	}

	@Override
	public net.neoforged.neoforge.client.ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand,
			ModelData data) {
		return net.neoforged.neoforge.client.ChunkRenderTypeSet.of(RenderType.solid(), RenderType.cutout(),
				RenderType.translucent());
	}

	public static void replace(Map<net.minecraft.client.resources.model.ModelResourceLocation, BakedModel> registry,
			Block block) {
		for (BlockState blockstate : block.getStateDefinition().getPossibleStates()) {
			net.minecraft.client.resources.model.ModelResourceLocation resLoc = BlockModelShaper
					.stateToModelLocation(blockstate);
			BakedModel original = registry.get(resLoc);
			BakedModel newModel = new SEMachineModel(original);
			registry.put(resLoc, newModel);
		}
	}
}
