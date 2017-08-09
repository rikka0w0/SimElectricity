package simelectricity.essential.machines.render;

import java.util.List;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import simelectricity.essential.common.ExtendedProperties;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;

public class SEMachineModel implements IPerspectiveAwareModel {
	private final IBakedModel[] firstState, secondState;
	private final boolean hasSecondState;
	public SEMachineModel(IBakedModel[] firstState, IBakedModel[] secondState){
		this.firstState = firstState;
		this.secondState = secondState;
		this.hasSecondState = secondState != null;
	}
	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState blockState, @Nullable EnumFacing side, long rand) {
	    if (!(blockState instanceof IExtendedBlockState)) {
		      return firstState[2].getQuads(blockState, side, rand);
		}
		
		//List<BakedQuad> quadsList = new LinkedList<BakedQuad>();
		
		IExtendedBlockState exBlockState = (IExtendedBlockState)blockState;
		EnumFacing facing = exBlockState.getValue(ExtendedProperties.propertyFacing);
	    
		if (facing == null)
			return firstState[2].getQuads(blockState, side, rand);
	    
		//System.out.println(facing);
		return firstState[facing.ordinal()].getQuads(blockState, side, rand);
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
	
	@Override
	public ItemOverrideList getOverrides() {
		return null;
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
}
