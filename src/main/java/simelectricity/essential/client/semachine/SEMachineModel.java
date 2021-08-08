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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import rikka.librikka.model.quadbuilder.MutableQuad;
import simelectricity.essential.api.ISECoverPanelHost;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.api.coverpanel.ISEFacadeCoverPanel;
import simelectricity.essential.client.coverpanel.GenericFacadeRender;
import simelectricity.essential.common.semachine.ISESocketProvider;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
		RenderType layer = MinecraftForgeClient.getRenderLayer();
		if (layer == null)
			return this.bakedModel.getQuads(state, side, rand, extraData);

		List<BakedQuad> quads = new LinkedList<>();

		boolean hideMachineFace = false;
		// Render facade cover panel, if possible
		ISECoverPanelHost host = extraData.getData(ISECoverPanelHost.prop);
		if (host != null && side != null && layer != RenderType.solid()) {
    		ISECoverPanel coverPanel = host.getCoverPanelOnSide(side);
    		if (coverPanel instanceof ISEFacadeCoverPanel) {
    			BlockState blockState = ((ISEFacadeCoverPanel) coverPanel).getBlockState();
    			BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);

    			model.getQuads(blockState, side, rand, EmptyModelData.INSTANCE).stream()
    				.map(MutableQuad::new)
    				.peek((mquad)->GenericFacadeRender.tintFunc(side, mquad))
    				.map(MutableQuad::bake)
    				.forEach(quads::add);

    			hideMachineFace = true;
    		}
		}

    	if (layer == RenderType.solid() && !hideMachineFace)
    		return this.bakedModel.getQuads(state, side, rand, extraData);
    	// Only render the machine body in the solid layer

		// Render the sockets in the cutout layer
		if (side == null && layer == RenderType.cutout()) {
    		ISESocketProvider sp = extraData.getData(ISESocketProvider.prop);
    		if (sp != null)
    			SocketRender.getBaked(quads, sp);
    	}

    	return quads;
	}

	public static void replace(Map<ResourceLocation, BakedModel> registry, Block block) {
		for (BlockState blockstate: block.getStateDefinition().getPossibleStates()) {
			ModelResourceLocation resLoc = BlockModelShaper.stateToModelLocation(blockstate);
			BakedModel original = registry.get(resLoc);
			BakedModel newModel = new SEMachineModel(original);
			registry.put(resLoc, newModel);
		}
	}
}
