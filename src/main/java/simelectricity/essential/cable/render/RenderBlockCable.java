package simelectricity.essential.cable.render;

import org.lwjgl.opengl.GL11;

import simelectricity.essential.api.ISECoverPanel;
import simelectricity.essential.api.ISEGenericCable;
import simelectricity.essential.cable.BlockCable;
import simelectricity.essential.utils.MatrixTranformations;
import simelectricity.essential.utils.SERenderHelper;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class RenderBlockCable implements ISimpleBlockRenderingHandler{
	public static int renderPass = -1;

	private final int renderID;
	
	public RenderBlockCable(){
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
		
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		ISEGenericCable cable = (ISEGenericCable)tileEntity;
		
		/////////////////////////////
		/// Render cover panels
		/////////////////////////////
		RenderHelperCoverPanel.renderCoverPanel(world, x, y, z, renderer, renderPass, cable);
		
		/////////////////////////////////
		/// Render center and branches
		/////////////////////////////////
		int meta = tileEntity.getBlockMetadata();
		double thickness = ((BlockCable)block).thickness[meta];
		IIcon insulatorTexture = block.getIcon(8, meta);
		IIcon copperTexture = block.getIcon(9, meta);
		int lightValue = block.getMixedBrightnessForBlock(world, x, y, z);
		
        int numOfCon = 0;
        ForgeDirection towardDir = null;
		
        //Render branches
        for (ForgeDirection side: ForgeDirection.VALID_DIRECTIONS){
        	if (cable.connectedOnSide(side)){
        		numOfCon++;
        		towardDir = side;
        		
        		double[][] branch = SERenderHelper.createSafeCopy(
        				((BlockCable)block).cableBranchModels[meta][side.ordinal()]);     		
        		
        		
        		SERenderHelper.translateCoord(branch, x+0.5, y+0.5, z+0.5);
        		SERenderHelper.addCubeToTessellator(branch, 
    					new IIcon[]{
    					null,
    					copperTexture,
    					insulatorTexture,
    					insulatorTexture,
    					insulatorTexture,
    					insulatorTexture,
    					}
    					, new int[]{
    					lightValue,
    					lightValue,
    					lightValue,
    					lightValue,
    					lightValue,
    					lightValue
    			});
        	}
        }
		
		//Render center
		double[][] center = SERenderHelper.createCubeVertexes(thickness, thickness, thickness);
		SERenderHelper.translateCoord(center, 0, -thickness/2, 0);
        IIcon[] centerTexture = new IIcon[6];
       	for (int i=0; i<6; i++)
    		centerTexture[i] = insulatorTexture;
        
        if (numOfCon == 1){
        	for (int i=0; i<6; i++)
        		centerTexture[i] = insulatorTexture;
        	
        	centerTexture[towardDir.getOpposite().ordinal()] = copperTexture;
        }else if (numOfCon > 1){
        	for (ForgeDirection side: ForgeDirection.VALID_DIRECTIONS)
        		if (cable.connectedOnSide(side))
        			centerTexture[side.ordinal()] = null;
        }
		
		SERenderHelper.translateCoord(center, x+0.5, y+0.5, z+0.5);
		SERenderHelper.addCubeToTessellator(center, centerTexture, new int[]{
				lightValue,
				lightValue,
				lightValue,
				lightValue,
				lightValue,
				lightValue
		});

		
		return false;	//Also make sure to only return true in renderWorldBlock() if you actually render something otherwise the game will crash.
	}

	public static void bakeCableModel(BlockCable block){
		block.cableBranchModels = new double[block.subNames.length][6][][];
		for (int variant=0; variant<block.subNames.length; variant++){
			double thickness = block.thickness[variant];
	        for (ForgeDirection side: ForgeDirection.VALID_DIRECTIONS){
	        	double[][] branch = SERenderHelper.createCubeVertexes(thickness, 0.5 -  thickness/2, thickness);
	        	SERenderHelper.translateCoord(branch, 0, thickness/2, 0);
	        	SERenderHelper.rotateCubeToDirection(branch, side);
	        	block.cableBranchModels[variant][side.ordinal()] = branch;
	        }
		}
	}
	
}
