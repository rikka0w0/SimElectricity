package simelectricity.essential.grid.render;

import simelectricity.essential.grid.BlockTransmissionTowerBottom;
import simelectricity.essential.utils.SERenderHeap;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RenderTransmissionTowerBottom implements ISimpleBlockRenderingHandler{
	private final int renderID;
	
	public RenderTransmissionTowerBottom(){
		renderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(renderID, this);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public int getRenderId() {
		return renderID;
	}
	
	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId,
			RenderBlocks renderer) {}

	
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		
		int lightValue = block.getMixedBrightnessForBlock(world, x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		int rotation = (meta&7)*45-90;
		int[] centerCoord = BlockTransmissionTowerBottom.getCenterBoxCoord(x, y, z, meta);
		
		//Direction indicator
		/*
		double[][] cube = SERenderHelper.createCubeVertexes(0.1, 1, 0.1);
		SERenderHelper.rotateToVec(cube, 0, 0, 0, 1, 0, 0);
		SERenderHelper.rotateAroundY(cube, rotation);
		SERenderHelper.translateCoord(cube, x+0.5, y, z+0.5);
		SERenderHelper.addCubeToTessellator(cube, SERenderHelper.createTextureArray(textures[2]), lightValue);
		*/
		
		
		
		SERenderHeap tower = Models.renderTower0Bottom(textures[2]);
		tower = tower.clone();

		//Calculate the rotation
		switch (meta&7){
		case 1:
			rotation=0;
			break;
		case 3:
			rotation=90;
			break;
		case 5:
			rotation=180;
			break;
		case 7:
			rotation=270;
			break;
			
		case 2:
			rotation=45;
			break;	
		case 4:
			rotation=135;
			break;
		case 6:
			rotation=225;
			break;
		case 0:
			rotation=315;
			break;	
		}
		
		tower.rotateAroundVector(rotation, 0, 1, 0);
		tower.transform(centerCoord[0]+0.5, centerCoord[1], centerCoord[2]+0.5);
		tower.applyToTessellator(lightValue);
		
		return true;
	}
	
	///////////////////////////////////
	/// Load texture for models
	///////////////////////////////////
	public static final IIcon[] textures = new IIcon[3];
	@SubscribeEvent
	public void eventHandler(TextureStitchEvent.Pre event){
		if (event.map.getTextureType() == 0){
	    	textures[0] = event.map.registerIcon("simelectricity:Wiring/CopperCable_Thin_Side");
	    	textures[1] = event.map.registerIcon("simelectricity:HvInsulator");
	    	textures[2] = event.map.registerIcon("simelectricity:AdjustableResistor_Top");
		}
	}
}
