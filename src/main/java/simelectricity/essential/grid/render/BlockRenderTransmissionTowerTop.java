package simelectricity.essential.grid.render;

import simelectricity.essential.grid.ISETransmissionTower;
import simelectricity.essential.utils.client.SERenderHeap;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockRenderTransmissionTowerTop implements ISimpleBlockRenderingHandler{
	private final int renderID;
	
	public BlockRenderTransmissionTowerTop(){
		renderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(renderID, this);
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
		/*
		 * Meta facing: MC: South - 0, OpenGL: Xpos(East) - 0
		 */
		int rotation = (meta&7)*45 - 90;
		
		SERenderHeap tower = null;
		
		if (meta>>3 == 0){
			tower = ModelBaker.model0.clone();

			TransmissionTowerRenderHelper helper = ((ISETransmissionTower) world.getTileEntity(x, y, z)).getRenderHelper();
			ModelBaker.renderInsulators(helper, x, y, z, rotation, lightValue);
			
			if (helper.render1() & helper.render2()){
				SERenderHeap insulator = ModelBaker.modelInsulator.clone();
				insulator.rotateAroundZ(180);
				insulator.transform(0, 7, 3.95);
				insulator.rotateAroundVector(rotation, 0, 1, 0);
				insulator.transform(x+0.5, y, z+0.5);
				insulator.applyToTessellator(lightValue);
			}
		}else{
			tower = ModelBaker.model1.clone();
		}
		
		tower = tower.clone();
		tower.rotateAroundY(rotation);
		tower.transform(x+0.5, y-18, z+0.5);
		tower.applyToTessellator(lightValue);
		
		//Direction indicator
		/*
		double[][] cube = SERenderHelper.createCubeVertexes(0.1, 1, 0.1);
		SERenderHelper.rotateToVec(cube, 0, 0, 0, 1, 0, 0);
		SERenderHelper.rotateAroundZ(cube, 45);
		SERenderHelper.rotateAroundVector(cube, 35, 1, 0, 1);
		SERenderHelper.translateCoord(cube, x+0.5, y, z+0.5);
		SERenderHelper.addCubeToTessellator(cube, SERenderHelper.createTextureArray(textures[2]), lightValue);
		*/
		
		return true;
	}
}
