package simelectricity.essential.client.grid.pole;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.client.BlockRenderModel;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.grid.EnumBlockTypePole3;
import simelectricity.essential.utils.client.SERawQuadCube;
import simelectricity.essential.utils.client.SERawQuadGroup;

@SideOnly(Side.CLIENT)
public class PowerPole3Model extends BlockRenderModel implements IPerspectiveAwareModel{
	private final TextureAtlasSprite textureMetal, textureInsulator, textureConcrete;
	private final EnumBlockTypePole3 blockType;
	private final int rotation;
	private final List<BakedQuad> quads = new LinkedList();
	
	private final SERawQuadGroup insulator;
		
	public PowerPole3Model(EnumBlockTypePole3 blockType, int rotation, TextureAtlasSprite textureMetal, TextureAtlasSprite textureInsulator, TextureAtlasSprite textureConcrete) {
		this.textureMetal = textureMetal;
		this.textureInsulator = textureInsulator;
		this.textureConcrete = textureConcrete;
		this.rotation = rotation;
		this.blockType = blockType;
		
		SERawQuadCube cube = new SERawQuadCube(0.25F, 1, 0.25F, textureConcrete);
		cube.translateCoord(0.5F, 0, 0.5F);
		cube.bake(quads);
		
		SERawQuadGroup insulator = null;
		//Build the insulator model
		switch (blockType) {
		case Pole:
			break;
		case Crossarm10kVT0:
		case Crossarm10kVT1:
			insulator = new SERawQuadGroup();
			insulator.add(new SERawQuadCube(0.08F, 0.5F, 0.08F, textureMetal));
			insulator.add((new SERawQuadCube(0.5F, 0.05F, 0.5F, textureInsulator)).translateCoord(0,0.15F,0));
			insulator.add((new SERawQuadCube(0.5F, 0.05F, 0.5F, textureInsulator)).translateCoord(0,0.225F,0));
			insulator.add((new SERawQuadCube(0.5F, 0.05F, 0.5F, textureInsulator)).translateCoord(0,0.3F,0));
			break;
		case Crossarm415VT0:
			insulator = new SERawQuadGroup();
			insulator.add(new SERawQuadCube(0.08F, 0.25F, 0.08F, textureMetal));
			insulator.add((new SERawQuadCube(0.25F, 0.05F, 0.25F, textureInsulator)).translateCoord(0,0.15F,0));
			break;
		}
		this.insulator = insulator;
		
		
		SERawQuadGroup model = new SERawQuadGroup();
		switch (blockType) {
		case Pole:
			break;
		case Crossarm10kVT0:
			model.add(new SERawQuadCube(0.15F, 0.1F, 1.6F, textureMetal));
			model.merge(insulator.clone().translateCoord(0, 0.05F, -0.74F));
			model.merge(insulator.clone().translateCoord(0, 0.05F, 0.74F));
			model.merge(insulator.clone().translateCoord(0, 1F, 0));
			break;
		case Crossarm10kVT1:
			model.add((new SERawQuadCube(0.15F, 0.08F, 1.6F, textureMetal)).translateCoord(0, 0.05F, 0));
			break;
		case Crossarm415VT0:
			model.add(new SERawQuadCube(0.15F, 0.1F, 1.94F, textureMetal));
			model.merge(insulator.clone().translateCoord(0, 0.05F, -0.9F));
			model.merge(insulator.clone().translateCoord(0, 0.05F, -0.45F));
			model.merge(insulator.clone().translateCoord(0, 0.05F, 0.45F));
			model.merge(insulator.clone().translateCoord(0, 0.05F, 0.9F));
			break;
		}
		
		model.rotateAroundY(rotation);
		model.translateCoord(0.5F, 0, 0.5F);
		model.bake(quads);
	}
	
	@Override
	public TextureAtlasSprite getParticleTexture() {return textureMetal;}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState blockState, EnumFacing side, long rand) {
		if (blockType == EnumBlockTypePole3.Crossarm10kVT1) {
		    PowerPoleRenderHelper helper = PowerPoleRenderHelper.fromState(blockState);
		    
		    if (helper == null)
		    	return quads;
		    
			LinkedList<BakedQuad> quads = new LinkedList();
			quads.addAll(this.quads);
		    helper.renderInsulator(insulator, quads);
		    
		    if (helper.connectionInfo.size() == 2) {
		    	insulator.clone().translateCoord(0.5F, 1F, 0.5F).bake(quads);
		    }
		    
		    return quads;
		}
		return quads;
	}

	@Override
	public boolean isGui3d() {return false;}
	
	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		ItemTransformVec3f half = new ItemTransformVec3f(new Vector3f(15, 255, 0), new Vector3f(), new Vector3f(0.5F, 0.5F, 0.5F));
		
		ItemCameraTransforms itemCameraTransforms = new ItemCameraTransforms(
	    		  half,
	    		  half,
	    		  new ItemTransformVec3f(new Vector3f(-15, 255, 0), new Vector3f(), new Vector3f(0.5F, 0.5F, 0.5F)),
	    		  new ItemTransformVec3f(new Vector3f(-15, 255, 0), new Vector3f(), new Vector3f(0.5F, 0.5F, 0.5F)),
	    		  half,
	    		  new ItemTransformVec3f(new Vector3f(45, 255-45, 0), new Vector3f(), new Vector3f(0.5F, 0.65F, 0.5F)),
	    		  half,
	    		  half);
		ItemTransformVec3f itemTransformVec3f = itemCameraTransforms.getTransform(cameraTransformType);
		TRSRTransformation tr = new TRSRTransformation(itemTransformVec3f);

	    return Pair.of(this, tr.getMatrix());
	}
}
