package simelectricity.Templates.Client.Render;

import simelectricity.api.client.ITransmissionTower;
import simelectricity.api.client.ITransmissionTowerRenderHelper;
import simelectricity.essential.utils.SERenderHeap;
import simelectricity.essential.utils.SERenderHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
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
public class RenderTower implements ISimpleBlockRenderingHandler{
	private final int renderID;
	
	public RenderTower(){
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

		TileEntity te = world.getTileEntity(x, y, z);
		if (!(te instanceof ITransmissionTower))
			return false;
		
		ITransmissionTowerRenderHelper helper = ((ITransmissionTower) te).getRenderHelper();
		
		if (helper == null)
			return false;
		
		int lightValue = block.getMixedBrightnessForBlock(world, x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		
		SERenderHeap tower = models[meta].clone();
		
		if (tower != null){
			tower.rotateAroundVector((float) helper.getRotation(), 0, 1, 0);
			tower.transform(x+0.5, y, z+0.5);
			tower.applyToTessellator(lightValue);
		}

		return false;
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
	
	///////////////////////////////////
	/// Compile steady parts
	///////////////////////////////////
	private static SERenderHeap[] models = new SERenderHeap[3];
	@SubscribeEvent
	public void eventHandler(TextureStitchEvent.Post event){
		if (event.map.getTextureType() == 0){
			models[0] = simelectricity.essential.grid.render.Models.renderTower0(textures[2]);
			
			models[1] = models[0].clone();
			SERenderHeap insulator = simelectricity.essential.grid.render.Models.renderInsulatorString(1.4, textures[1]);
			double[][] rod = SERenderHelper.createCubeVertexes(0.1, 1.95, 0.1);
			SERenderHelper.translateCoord(rod, 0, -0.15, 0);
			insulator.addCube(rod, textures[2]);
			models[1].appendHeap(insulator.clone().transform(0,18-1.85,-4.9));
			models[1].appendHeap(insulator.clone().transform(0,18-1.85,4.9));
			models[1].appendHeap(insulator.transform(0,23.15,3.95));
		}
	}
}
