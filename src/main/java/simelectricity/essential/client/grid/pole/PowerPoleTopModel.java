package simelectricity.essential.client.grid.pole;

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
import simelectricity.essential.client.grid.ISEPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.common.UnlistedNonNullProperty;
import simelectricity.essential.utils.client.SERenderHeap;
import simelectricity.essential.utils.client.SERenderHelper;

@SideOnly(Side.CLIENT)
public class PowerPoleTopModel extends BlockRenderModel {
	private final TextureAtlasSprite textureMetal;
	private final TextureAtlasSprite textureInsulator;
	private final LinkedList<BakedQuad> quads;
	private final LinkedList<BakedQuad> insulator;
	private final int type;
	private final SERenderHeap modelInsulator;
	
	public PowerPoleTopModel(int facing, int type, TextureAtlasSprite textureMetal, TextureAtlasSprite textureInsulator) {
		this.textureMetal = textureMetal;
		this.textureInsulator = textureInsulator;
		this.quads = new LinkedList();
		this.insulator = new LinkedList();
		this.type = type;
		
		/*
		 * Meta facing: MC: South - 0, OpenGL: Xpos(East) - 0
		 */
		int rotation = facing*45 - 90;
		SERenderHeap model = Models.renderTower0Top(textureMetal);
		
		if (type == 0) {
			this.modelInsulator = Models.renderInsulatorString(1.4, textureInsulator);
			double[][] rod2 = SERenderHelper.createCubeVertexes(0.1, 2, 0.1);				
			SERenderHelper.translateCoord(rod2, 0, -0.3, 0);
			modelInsulator.addCube(rod2, textureMetal);
			modelInsulator.transform(0, 0.3, 0);
			
			SERenderHeap insulatorHeap = modelInsulator.clone();
			insulatorHeap.rotateAroundZ(180);
			insulatorHeap.transform(0, 7, 3.95);
			insulatorHeap.rotateAroundVector(rotation, 0, 1, 0);
			insulatorHeap.transform(0.5, 0, 0.5);
			insulatorHeap.bake(insulator);
		}else {
			this.modelInsulator = null;
			
			SERenderHeap insulator = Models.renderInsulatorString(1.4, textureInsulator);
			double[][] rod = SERenderHelper.createCubeVertexes(0.1, 1.95, 0.1);
			SERenderHelper.translateCoord(rod, 0, -0.15, 0);
			insulator.addCube(rod, textureMetal);
			model.appendHeap(insulator.clone().transform(0,18-1.85,-4.9));
			model.appendHeap(insulator.clone().transform(0,18-1.85,4.9));
			model.appendHeap(insulator.transform(0,23.15,3.95));
		}
		
		model.rotateAroundY(rotation).transform(0.5, -18, 0.5).bake(quads);
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {return textureMetal;}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState blockState, EnumFacing side, long rand) {
		if (MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.SOLID) {
			if (type == 1)
				return ImmutableList.copyOf(this.quads);
			
			LinkedList<BakedQuad> quads = new LinkedList();
			quads.addAll(this.quads);
			
		    if (!(blockState instanceof IExtendedBlockState))
		    	//Normally this should not happen, just in case, to prevent crashing
		    	return ImmutableList.of();
		    
		    IExtendedBlockState exBlockState = (IExtendedBlockState)blockState;
		    WeakReference<ISEGridTile> ref = exBlockState.getValue(UnlistedNonNullProperty.propertyGridTile);
		    ISEGridTile gridTile = ref==null ? null : ref.get();
			
		    if (!(gridTile instanceof ISEPowerPole))
		    	//Normally this should not happen, just in case, to prevent crashing
		    	return ImmutableList.of();
		    
		    
		    PowerPoleRenderHelper helper = ((ISEPowerPole)gridTile).getRenderHelper();
		    
		    if (helper == null)
		    	return quads;	//Before the new placed block receiving the update packet from server;
		    
		    for (PowerPoleRenderHelper.ConnectionInfo[] connections: helper.connectionInfo) {
		    	for (PowerPoleRenderHelper.ConnectionInfo connection: connections) {
		    		Models.renderInsulators(helper.pos, connection.from, connection.fixedTo, connection.insulatorAngle, modelInsulator, quads);
		    	}
		    }
			
			if (helper.connectionInfo.size() > 1)
				quads.addAll(insulator);
				
			return quads;
		}
		
		return ImmutableList.of();
	}
	

}
