package simelectricity.essential.client.semachine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import simelectricity.essential.common.semachine.ExtendedProperties;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SEMachineModel implements IPerspectiveAwareModel {
	private final IBakedModel[] firstState, secondState;
	private final boolean hasSecondState;
	public SEMachineModel(IBakedModel[] firstState, IBakedModel[] secondState){
		this.firstState = firstState;
		this.secondState = secondState;
		this.hasSecondState = secondState != null;
	}
	
	@Override
	public boolean isAmbientOcclusion() {
		return firstState[2].isAmbientOcclusion();
	}
	@Override
	public boolean isGui3d() {
		return firstState[2].isGui3d();
	}
	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}
	@Override
	public TextureAtlasSprite getParticleTexture() {
		return firstState[2].getParticleTexture();
	}
	@Override
	@Deprecated
	public ItemCameraTransforms getItemCameraTransforms() {
		return firstState[2].getItemCameraTransforms();
	}
	
	private final ItemOverrideList overrideList = new ItemOverrideList(new ArrayList<ItemOverride>(0));
	@Override
	public ItemOverrideList getOverrides() {
		return overrideList;	//I'm not sure what this thing does QAQ, only know this prevents crashing 233
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
		if (firstState[2] instanceof IPerspectiveAwareModel) {
	      Matrix4f matrix4f = ((IPerspectiveAwareModel)firstState[2]).handlePerspective(cameraTransformType).getRight();
	      return Pair.of(this, matrix4f);
	    } else {
	      // If the parent model isn't an IPerspectiveAware, we'll need to generate the correct matrix ourselves using the
	      //  ItemCameraTransforms.

	      ItemCameraTransforms itemCameraTransforms = firstState[2].getItemCameraTransforms();
	      ItemTransformVec3f itemTransformVec3f = itemCameraTransforms.getTransform(cameraTransformType);
	      TRSRTransformation tr = new TRSRTransformation(itemTransformVec3f);
	      Matrix4f mat = null;
	      if (tr != null) { // && tr != TRSRTransformation.identity()) {
	        mat = tr.getMatrix();
	      }
	      // The TRSRTransformation for vanilla items have blockCenterToCorner() applied, however handlePerspective
	      //  reverses it back again with blockCornerToCenter().  So we don't need to apply it here.

	      return Pair.of(this, mat);
	    }
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
	    	//Normally this should not happen, just in case, to prevent crashing
	    	return firstState[2].getQuads(blockState, side, rand);
				
		IExtendedBlockState exBlockState = (IExtendedBlockState)blockState;
		EnumFacing facing = exBlockState.getValue(ExtendedProperties.propertyFacing);
	    boolean is2State = exBlockState.getValue(ExtendedProperties.propertIs2State);
	    int[] socketIcon = getSocketIconArray(exBlockState);
	    
		if (facing == null)
			return firstState[2].getQuads(blockState, side, rand);
	    
		List<BakedQuad> selectedModel = is2State ? 
				secondState[facing.ordinal()].getQuads(blockState, side, rand) :
				firstState[facing.ordinal()].getQuads(blockState, side, rand);
		
		List<BakedQuad> quads = new LinkedList<BakedQuad>();
		quads.addAll(selectedModel);
        
        SocketRender.getBaked(quads, socketIcon);
        
		return quads;
	}
	
	private final TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks()
            .getAtlasSprite("minecraft:blocks/diamond_block");
}
