package simelectricity.essential.client.semachine;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import simelectricity.essential.common.semachine.ISESocketProvider;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public final class SEMachineModel implements IDynamicBakedModel {
    private final IBakedModel bakedModel;

    public SEMachineModel(IBakedModel bakedModel) {
        this.bakedModel = bakedModel;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.bakedModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.bakedModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.bakedModel.getParticleTexture();
    }

    @Override
    @Deprecated
    public ItemCameraTransforms getItemCameraTransforms() {
        return this.bakedModel.getItemCameraTransforms();
    }
    
	@Override
	public boolean func_230044_c_() {
		return this.bakedModel.func_230044_c_();
	}

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }
    
	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
		// Create a copy of the original quads
		List<BakedQuad> quads = new LinkedList<>(this.bakedModel.getQuads(state, side, rand, extraData));
    	
		if (side == null) {
    		ISESocketProvider sp = extraData.getData(ISESocketProvider.prop);
    		if (sp != null)
    			SocketRender.getBaked(quads, sp);
    	}
    	
    	return quads;
	}
	
	public static void replace(Map<ResourceLocation, IBakedModel> registry, Block block) {
		for (BlockState blockstate: block.getStateContainer().getValidStates()) {
			ModelResourceLocation resLoc = BlockModelShapes.getModelLocation(blockstate);
			IBakedModel original = registry.get(resLoc);
			IBakedModel newModel = new SEMachineModel(original);
			registry.put(resLoc, newModel);
		}
	}
}
