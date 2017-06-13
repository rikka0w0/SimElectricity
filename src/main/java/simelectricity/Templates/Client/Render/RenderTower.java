package simelectricity.Templates.Client.Render;

import simelectricity.essential.utils.SERenderHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class RenderTower implements ISimpleBlockRenderingHandler{
	private final int renderID;
	
	public RenderTower(){
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
			RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {

		IIcon texture = Block.getBlockFromName("gold_ore").getIcon(0, 0);
		
		int lightValue = block.getMixedBrightnessForBlock(world, x, y, z);
		
		double[][] insulator = SERenderHelper.createCubeVertexes(0.1, 1, 0.1);
		SERenderHelper.translateCoord(insulator, x+0.5, y+0.5, z+0.5);
		
		
		SERenderHelper.addCubeToTessellator(insulator, 
				new IIcon[]{
				texture,
				texture,
				texture,
				texture,
				texture,
				texture,
				}
				, new int[]{
				lightValue,
				lightValue,
				lightValue,
				lightValue,
				lightValue,
				lightValue
		});
		
		return false;
	}




	public static void bakeModel(){
		
	}
}
