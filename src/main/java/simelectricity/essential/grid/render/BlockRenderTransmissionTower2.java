package simelectricity.essential.grid.render;

import simelectricity.essential.grid.BlockTransmissionTower2;
import simelectricity.essential.grid.ISETransmissionTower;
import simelectricity.essential.utils.client.SERenderHeap;
import simelectricity.essential.utils.client.SERenderHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockRenderTransmissionTower2 implements ISimpleBlockRenderingHandler{
	private final int renderID;
	
	public BlockRenderTransmissionTower2(){
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
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {}
	
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		int lightValue = block.getMixedBrightnessForBlock(world, x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		/*
		 * Meta facing: MC: South - 0, OpenGL: Xpos(East) - 0
		 */
		int rotation = (meta&3)*90 - 90;
		boolean isRod = (meta & 4) > 0;
	
		//Direction indicator
		/*
		double[][] cube = SERenderHelper.createCubeVertexes(0.1, 1, 0.1);
		SERenderHelper.rotateToVec(cube, 0, 0, 0, 1, 0, 0);
		SERenderHelper.rotateAroundY(cube, rotation);
		SERenderHelper.translateCoord(cube, x+0.5, y, z+0.5);
		SERenderHelper.addCubeToTessellator(cube, SERenderHelper.createTextureArray(BlockRenderTransmissionTowerTop.textureInsulator), lightValue);
		*/
		
		
	
		if (isRod){
			double[][] cube = SERenderHelper.createCubeVertexes(0.25, 1, 0.25);
			SERenderHelper.rotateAroundY(cube, rotation);
			SERenderHelper.translateCoord(cube, x+0.5, y, z+0.5);
			SERenderHelper.addCubeToTessellator(cube, SERenderHelper.createTextureArray(ModelBaker.textureMetal), lightValue);
		}else{
			SERenderHeap tower;
			if (BlockTransmissionTower2.typeFromMeta(meta)){	//1
				tower = ModelBaker.model3.clone();
				
				
			}else{	//0
				tower = ModelBaker.model2.clone();

				TransmissionTowerRenderHelper helper = ((ISETransmissionTower) world.getTileEntity(x, y, z)).getRenderHelper();
				ModelBaker.renderInsulators(helper, x, y, z, rotation, lightValue);
			}
			tower.rotateAroundY(rotation);
			tower.transform(x+0.5, y, z+0.5);
			tower.applyToTessellator(lightValue);
		}
		
		return true;
	}
	
	///////////////////////////////////
	/// Load texture for models
	///////////////////////////////////
	public static IIcon textureInsulator, textureMetal;
	@SubscribeEvent
	public void eventHandler(TextureStitchEvent.Pre event){
		if (event.map.getTextureType() == 0){
			textureInsulator = event.map.registerIcon("sime_essential:transmission/glass_insulator");
			textureMetal = event.map.registerIcon("sime_essential:transmission/metal");
		}
	}
}
