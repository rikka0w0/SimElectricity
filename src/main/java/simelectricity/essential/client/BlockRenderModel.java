package simelectricity.essential.client;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;

public abstract class BlockRenderModel implements IBakedModel {
	@Override
	public boolean isAmbientOcclusion() {return false;}
	
	@Override
	public boolean isGui3d() {return false;}
	
	@Override
	public boolean isBuiltInRenderer() {return false;}
	
	@Override
	public ItemCameraTransforms getItemCameraTransforms() {return ItemCameraTransforms.DEFAULT;}
	
	@Override
	public ItemOverrideList getOverrides() {return ItemOverrideList.NONE;}
}
