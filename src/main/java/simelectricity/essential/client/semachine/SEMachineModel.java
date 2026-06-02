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
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType renderType) {
		if (renderType == null)
			return this.bakedModel.getQuads(state, side, rand);

		List<BakedQuad> quads = new LinkedList<>();

		boolean hideMachineFace = false;
		// Render facade cover panel, if possible
		ISECoverPanelHost host = extraData.get(ISECoverPanelHost.prop);
		if (host != null && side != null && renderType != RenderType.solid()) {
    		ISECoverPanel coverPanel = host.getCoverPanelOnSide(side);
    		if (coverPanel instanceof ISEFacadeCoverPanel) {
    			BlockState blockState = ((ISEFacadeCoverPanel) coverPanel).getBlockState();
    			BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);

    			model.getQuads(blockState, side, rand).stream()
    				.map(MutableQuad::new)
    				.peek((mquad)->GenericFacadeRender.tintFunc(side, mquad))
    				.map(MutableQuad::bake)
    				.forEach(quads::add);

    			hideMachineFace = true;
    		}
		}

    	if (renderType == RenderType.solid() && !hideMachineFace)
    		return this.bakedModel.getQuads(state, side, rand);
    	// Only render the machine body in the solid layer

		// Render the sockets in the cutout layer
		if (side == null && renderType == RenderType.cutout()) {
    		ISESocketProvider sp = extraData.get(ISESocketProvider.prop);
    		if (sp != null)
    			SocketRender.getBaked(quads, sp);
    	}

    	return quads;
	}



	public static void replace(Map<net.minecraft.client.resources.model.ModelResourceLocation, BakedModel> registry, Block block) {
		for (BlockState blockstate: block.getStateDefinition().getPossibleStates()) {
			net.minecraft.client.resources.model.ModelResourceLocation resLoc = BlockModelShaper.stateToModelLocation(blockstate);
			BakedModel original = registry.get(resLoc);
			BakedModel newModel = new SEMachineModel(original);
			registry.put(resLoc, newModel);
		}
	}
}
