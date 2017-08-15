package simelectricity.essential.client.grid;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.tile.ISEGridTile;

import simelectricity.essential.client.BlockRenderModel;
import simelectricity.essential.common.UnlistedNonNullProperty;
import simelectricity.essential.utils.client.SERawQuadCube;
import simelectricity.essential.utils.client.SERenderHeap;
import simelectricity.essential.utils.client.SERenderHelper;

@SideOnly(Side.CLIENT)
public class TransmissionTower2Model extends BlockRenderModel {
	private final TextureAtlasSprite textureMetal;
	private final TextureAtlasSprite textureInsulator;
	private final LinkedList<BakedQuad> quads;
	private final SERenderHeap modelInsulator;
	private final int type;
	private final boolean isRod;
	
	public TransmissionTower2Model(int facing, int type, boolean isRod, TextureAtlasSprite textureMetal, TextureAtlasSprite textureInsulator) {
		this.textureMetal = textureMetal;
		this.textureInsulator = textureInsulator;
		this.quads = new LinkedList();
		this.type = type;
		this.isRod = isRod;
		
		/*
		 * Meta facing: MC: South - 0, OpenGL: Xpos(East) - 0
		 */
		int rotation = facing*90 - 90;
		if (isRod){
			this.modelInsulator = null;
			
			SERawQuadCube cube = new SERawQuadCube(0.25F, 1, 0.25F, textureMetal);
			cube.translateCoord(0.5F, 0, 0.5F);
			cube.bake(quads);
		}else{
			SERenderHeap model2 = new SERenderHeap();
			double[][] cube = SERenderHelper.createCubeVertexes(0.25, 11, 0.25);
			SERenderHelper.translateCoord(cube, 0, -5.5, 0);
			SERenderHelper.rotateAroundX(cube, 90);
			SERenderHelper.translateCoord(cube, 0.25, 0.125, 0);
			model2.addCube(cube, textureMetal);
			cube = SERenderHelper.createCubeVertexes(0.25, 11, 0.25);
			SERenderHelper.translateCoord(cube, 0, -5.5, 0);
			SERenderHelper.rotateAroundX(cube, 90);
			SERenderHelper.translateCoord(cube, -0.25, 0.125, 0);
			model2.addCube(cube, textureMetal);
			
			
			if (type>0){	//1
				this.modelInsulator = null;
				
				SERenderHeap insulator = Models.renderInsulatorString(1.4, textureInsulator);
				double[][] rod = SERenderHelper.createCubeVertexes(0.1, 1.95, 0.1);
				SERenderHelper.translateCoord(rod, 0, -0.15, 0);
				insulator.addCube(rod, textureMetal);
				model2.appendHeap(insulator.clone().transform(0, 0.125-1.8, -4.5));
				model2.appendHeap(insulator.clone().transform(0, 0.125-1.8, 0));
				model2.appendHeap(insulator.transform(0, 0.125-1.8, 4.5));
			}else {
				this.modelInsulator = Models.renderInsulatorString(1.4, textureInsulator);
				double[][] rod2 = SERenderHelper.createCubeVertexes(0.1, 2, 0.1);				
				SERenderHelper.translateCoord(rod2, 0, -0.3, 0);
				modelInsulator.addCube(rod2, textureMetal);
				modelInsulator.transform(0, 0.3, 0);
			}
			
			model2.rotateAroundY(rotation).transform(0.5, 0, 0.5).bake(quads);
		}
	}
	
	@Override
	public TextureAtlasSprite getParticleTexture() {return textureMetal;}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState blockState, EnumFacing side, long rand) {
		if (MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.SOLID) {
			if (isRod || type == 1)
				return ImmutableList.copyOf(this.quads);
			
			LinkedList<BakedQuad> quads = new LinkedList();
			quads.addAll(this.quads);
			
		    if (!(blockState instanceof IExtendedBlockState))
		    	//Normally this should not happen, just in case, to prevent crashing
		    	return ImmutableList.of();
		    
		    IExtendedBlockState exBlockState = (IExtendedBlockState)blockState;
		    WeakReference<ISEGridTile> ref = exBlockState.getValue(UnlistedNonNullProperty.propertyGridTile);
		    ISEGridTile gridTile = ref==null ? null : ref.get();
			
		    if (!(gridTile instanceof ISETransmissionTower))
		    	//Normally this should not happen, just in case, to prevent crashing
		    	return ImmutableList.of();
		    
		    TransmissionTowerRenderHelper helper = ((ISETransmissionTower)gridTile).getRenderHelper();
			if (helper.render1())
				TransmissionTowerTopModel.renderInsulators(helper.getPosOffset(), helper.from1(), helper.to1(), helper.angle1(), modelInsulator, quads);
			if (helper.render2())
				TransmissionTowerTopModel.renderInsulators(helper.getPosOffset(),helper.from2(), helper.to2(), helper.angle2(), modelInsulator, quads);
		    
		    return quads;
		}
		
		return ImmutableList.of();
	}
}
