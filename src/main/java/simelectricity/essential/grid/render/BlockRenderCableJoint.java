package simelectricity.essential.grid.render;

import simelectricity.essential.utils.client.SERenderHeap;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class BlockRenderCableJoint implements ISimpleBlockRenderingHandler{
	private final int renderID;
	
	public BlockRenderCableJoint(){
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
		
		
		SERenderHeap body = model.clone();
		
		body.rotateAroundY(rotation);
		body.transform(x+0.5, y, z+0.5);
		body.applyToTessellator(lightValue);
		
		return true;
	}
	
	///////////////////////////////////
	/// Load texture for models
	///////////////////////////////////
	public static final IIcon[] textures = new IIcon[3];
	@SubscribeEvent
	public void eventHandler(TextureStitchEvent.Pre event){
		if (event.map.getTextureType() == 0){
	    	textures[0] = event.map.registerIcon("sime_essential:transmission/essential_cable_joint_texture_updown");
	    	textures[1] = event.map.registerIcon("sime_essential:transmission/essential_cable_joint_texture_metal");
	    	textures[2] = event.map.registerIcon("sime_essential:transmission/essential_cable_joint_texture_side");
		}
	}
	
	///////////////////////////////////
	/// Compile steady parts
	///////////////////////////////////
	private static SERenderHeap model;
	
	@SubscribeEvent
	public void eventHandler(TextureStitchEvent.Post event){
		if (event.map.getTextureType() == 0){
			model = Models.renderCableJoint(textures);
		}
	}
}
