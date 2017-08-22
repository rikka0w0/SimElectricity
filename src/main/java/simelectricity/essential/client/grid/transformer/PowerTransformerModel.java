package simelectricity.essential.client.grid.transformer;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import simelectricity.essential.client.BlockRenderModel;
import simelectricity.essential.client.grid.Models;
import simelectricity.essential.utils.client.SERenderHeap;
import simelectricity.essential.utils.client.SERenderHelper;

public class PowerTransformerModel extends BlockRenderModel {
	private final int rotation;
	private final boolean mirrored;
	private final IBakedModel model;
	private final LinkedList<BakedQuad> insulator = new LinkedList();
	
	private final TextureAtlasSprite textureMetal, textureInsulator;
	
	public final static int[] rotationMatrix = new int[] {4,0,6,2};	//NSWE
	
	public PowerTransformerModel(int facing, boolean mirrored, IBakedModel bakedModel, TextureAtlasSprite textureMetal, TextureAtlasSprite textureInsulator) {
		this.rotation = rotationMatrix[facing]*45 - 90;
		this.mirrored = mirrored;
		this.model = bakedModel;
		this.textureMetal = textureMetal;
		this.textureInsulator = textureInsulator;
		
		//Rotation is done first (with the .obj loader), so we need to figure out the reflection axis
		int[] xf = new int[] {1,1,-1,-1};
		int[] zf = new int[] {-1,-1,1, 1};
		int a = 1;
		int b = 1;
		
		if (mirrored) {
			a = xf[facing];
			b = zf[facing];
		}
		
		SERenderHeap model = new SERenderHeap();
		SERenderHeap insulator = Models.renderInsulatorString(1.4, textureInsulator);
		double[][] rod = SERenderHelper.createCubeVertexes(0.1, 1.8, 0.1);
		SERenderHelper.translateCoord(rod, 0, -0.1, 0);
		insulator.addCube(rod, textureMetal);
		insulator.transform(0, 0.1, 0);
		model.appendHeap(insulator.clone()	.transform(a,1,-1.5*b));
		model.appendHeap(insulator.clone()	.transform(a,1,0*b));
		model.appendHeap(insulator			.transform(a,1,1.5*b));
		
		insulator = Models.renderInsulatorString(0.7, textureInsulator);
		rod = SERenderHelper.createCubeVertexes(0.1, 1.1, 0.1);
		SERenderHelper.translateCoord(rod, 0, -0.1, 0);
		insulator.addCube(rod, textureMetal);
		insulator.transform(0, 0.1, 0);
		model.appendHeap(insulator.clone()	.transform(-a,1,0.2*b));
		model.appendHeap(insulator.clone()	.transform(-a,1,1*b));
		model.appendHeap(insulator			.transform(-a,1,1.8*b));
		model.rotateAroundY(rotation).transform(0.5, 0, 0.5).bake(this.insulator);
	}
	
	@Override
	public TextureAtlasSprite getParticleTexture() {
		return model.getParticleTexture();
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		List<BakedQuad> quads = new LinkedList<BakedQuad>();
		quads.addAll(model.getQuads(state, side, rand));
		quads.addAll(this.insulator);
		return quads;
	}
}
