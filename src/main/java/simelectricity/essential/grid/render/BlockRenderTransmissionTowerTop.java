package simelectricity.essential.grid.render;

import simelectricity.essential.api.ISETransmissionTower;
import simelectricity.essential.utils.client.SERenderHeap;
import simelectricity.essential.utils.client.SERenderHelper;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockRenderTransmissionTowerTop implements ISimpleBlockRenderingHandler{
	private final int renderID;
	
	public BlockRenderTransmissionTowerTop(){
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
			RenderBlocks renderer) {

	}

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
			tower = model0.clone();
			
			TransmissionTowerRenderHelper helper = ((ISETransmissionTower) world.getTileEntity(x, y, z)).getRenderHelper();
			
			if (helper.render1())
				renderInsulators(helper.from1(), helper.to1(), helper.angle1(), lightValue);	
			if (helper.render2())
				renderInsulators(helper.from2(), helper.to2(), helper.angle2(), lightValue);			
	
			if (helper.render1() & helper.render2()){
				SERenderHeap insulator = modelInsulator.clone();
				insulator.rotateAroundZ(180);
				insulator.transform(0, 7, 3.95);
				insulator.rotateAroundVector(rotation, 0, 1, 0);
				insulator.transform(x+0.5, y, z+0.5);
				insulator.applyToTessellator(lightValue);
			}
		}else{
			tower = model1.clone();
		}
		
		tower = tower.clone();
		tower.rotateAroundVector(rotation, 0, 1, 0);
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

	private static void renderInsulators(double[] from, double[] to, double[] angle, int lightValue){
		for (int i=0; i<3; i++){
			SERenderHeap insulator = modelInsulator.clone();
			
			insulator.rotateAroundZ((float) (angle[i]/Math.PI*180));
			insulator.rotateToVec(from[3*i],from[3*i+1],from[3*i+2], to[3*i],from[3*i+1],to[3*i+2]);
			insulator.transform(from[3*i], from[3*i+1],from[3*i+2]);
			insulator.applyToTessellator(lightValue);	
		}
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
	
	///////////////////////////////////
	/// Compile steady parts
	///////////////////////////////////
	private static SERenderHeap model0;
	private static SERenderHeap model1;
	private static SERenderHeap modelInsulator;
	
	@SubscribeEvent
	public void eventHandler(TextureStitchEvent.Post event){
		if (event.map.getTextureType() == 0){
			model0 = simelectricity.essential.grid.render.Models.renderTower0Top(textureMetal);
			
			model1 = model0.clone();
			SERenderHeap insulator = Models.renderInsulatorString(1.4, textureInsulator);
			double[][] rod = SERenderHelper.createCubeVertexes(0.1, 1.95, 0.1);
			SERenderHelper.translateCoord(rod, 0, -0.15, 0);
			insulator.addCube(rod, textureMetal);
			model1.appendHeap(insulator.clone().transform(0,18-1.85,-4.9));
			model1.appendHeap(insulator.clone().transform(0,18-1.85,4.9));
			model1.appendHeap(insulator.transform(0,23.15,3.95));
		
			modelInsulator = Models.renderInsulatorString(1.4, textureInsulator);
			double[][] rod2 = SERenderHelper.createCubeVertexes(0.1, 2, 0.1);				
			SERenderHelper.translateCoord(rod2, 0, -0.3, 0);
			modelInsulator.addCube(rod2, textureMetal);
			modelInsulator.transform(0, 0.3, 0);
		}
	}
}
