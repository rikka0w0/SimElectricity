package simelectricity.essential.machines.render;

import org.lwjgl.opengl.GL11;

import simelectricity.essential.utils.client.SERenderHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class BlockRenderMachine implements ISimpleBlockRenderingHandler{
	private final int renderID;
	
	public BlockRenderMachine(){
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
		return true;
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId,
			RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		
		block.setBlockBoundsForItemRender();
		renderer.setRenderBoundsFromBlock(block);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (! (te instanceof ISESocketProvider))
			return false;
		
		ISESocketProvider socketProvider = (ISESocketProvider) te;
		
		boolean drewSomething = false;
		IIcon[] textures = SERenderHelper.createTextureArray(null);
		for (ForgeDirection side: ForgeDirection.VALID_DIRECTIONS){
			int iconIndex = socketProvider.getSocketIconIndex(side);
			if (iconIndex < 0)
				continue;
			
			if (iconIndex > iconCache.length - 1)
				iconIndex = iconCache.length - 1;
			
			textures[side.ordinal()] = iconCache[iconIndex];
			drewSomething = true;
		}
		
		int lightValue = block.getMixedBrightnessForBlock(world, x, y, z);
		double[][] cube = SERenderHelper.createCubeVertexes(1.001, 1.002, 1.001);
		SERenderHelper.translateCoord(cube, x+0.5, y-0.001, z+0.5);
		SERenderHelper.addCubeToTessellator(cube, textures, lightValue);
    	
		//Draw the original block
		renderer.renderStandardBlock(block, x, y, z);
		return true;
	}
	
	///////////////////////////////////
	/// Load texture for models
	///////////////////////////////////
	public static final IIcon[] iconCache = new IIcon[5];
	@SubscribeEvent
	public void eventHandler(TextureStitchEvent.Pre event){
		if (event.map.getTextureType() == 0){
			for (int i=0; i<iconCache.length; i++)
				iconCache[i] = event.map.registerIcon("sime_essential:sockets/" + String.valueOf(i));
		}
	}
}
