package simelectricity.essential.client.semachine;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import simelectricity.essential.common.semachine.ExtendedProperties;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SEMachineModel implements IBakedModel {
	private final IBakedModel bakedModel;
	public SEMachineModel(IBakedModel bakedModel){
		this.bakedModel = bakedModel;
	}
	
	@Override
	public boolean isAmbientOcclusion() {
		return bakedModel.isAmbientOcclusion();
	}
	@Override
	public boolean isGui3d() {
		return bakedModel.isGui3d();
	}
	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}
	@Override
	public TextureAtlasSprite getParticleTexture() {
		return bakedModel.getParticleTexture();
	}
	@Override
	@Deprecated
	public ItemCameraTransforms getItemCameraTransforms() {
		return bakedModel.getItemCameraTransforms();
	}
	
	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.NONE;	//I'm not sure what this thing does QAQ, only know this prevents crashing 233
	}
	
	private int[] getSocketIconArray(IExtendedBlockState exBlockState){
		int[] ret = new int[6];
		int i = 0;
		for (IUnlistedProperty<Integer> prop: ExtendedProperties.propertySockets){
			int val = exBlockState.getValue(prop);
			ret[i] = val - 1;	//-1: no socket icon
			i++;
		}
		return ret;
	}
	
	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState blockState, @Nullable EnumFacing side, long rand) {
	    if (!(blockState instanceof IExtendedBlockState))
	    	return bakedModel.getQuads(blockState, side, rand);	//Item Model
				
		IExtendedBlockState exBlockState = (IExtendedBlockState)blockState;
	    int[] socketIcon = getSocketIconArray(exBlockState);
	    
	    
		List<BakedQuad> quads = new LinkedList<BakedQuad>();
		quads.addAll(bakedModel.getQuads(blockState, side, rand));
        
        SocketRender.getBaked(quads, socketIcon);
        
		return quads;
	}
}
