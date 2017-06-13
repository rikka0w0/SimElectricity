package simelectricity.essential.cable.render;

import simelectricity.essential.api.ISECoverPanel;
import simelectricity.essential.api.ISEGenericCable;
import simelectricity.essential.cable.BlockCable;
import simelectricity.essential.utils.MatrixTranformations;

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


/**
 * Part of this source code is from BuildCraft
 * <p/>
 * Special thanks to SpaceToad and the BuildCraft Team
 * @author Rikka0_0
 */
@Deprecated
@SideOnly(Side.CLIENT)
public class RenderBlockCable implements ISimpleBlockRenderingHandler{
	public static int renderPass = -1;

	private final int renderID;
	
	private static final float[] xOffsets = new float[6];
	private static final float[] yOffsets = new float[6];
	private static final float[] zOffsets = new float[6];
	
	private static final float[][][] coverPanelMatrix = new float[6][3][2];
	
	public RenderBlockCable(){
		renderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(renderID, this);
		
		BlockCable.renderID = renderID;
		
		xOffsets[0] = 1F / 4096F;
		xOffsets[1] = xOffsets[0];
		xOffsets[2] = 0;
		xOffsets[3] = 0;
		xOffsets[4] = 0;
		xOffsets[5] = 0;

		yOffsets[0] = 0;
		yOffsets[1] = 0;
		yOffsets[2] = xOffsets[0];
		yOffsets[3] = xOffsets[0];
		yOffsets[4] = 0;
		yOffsets[5] = 0;

		zOffsets[0] = xOffsets[0];
		zOffsets[1] = xOffsets[0];
		zOffsets[2] = 0;
		zOffsets[3] = 0;
		zOffsets[4] = 0;
		zOffsets[5] = 0;
		
		for (ForgeDirection direction: ForgeDirection.VALID_DIRECTIONS){
			coverPanelMatrix[direction.ordinal()][0][0] = 0.0F;
			coverPanelMatrix[direction.ordinal()][0][1] = 1.0F;
			// Y START - END
			coverPanelMatrix[direction.ordinal()][1][0] = 0.0F;
			coverPanelMatrix[direction.ordinal()][1][1] = (float) BlockCable.coverPanelThickness;
			// Z START - END
			coverPanelMatrix[direction.ordinal()][2][0] = 0.0F;
			coverPanelMatrix[direction.ordinal()][2][1] = 1.0F;
			MatrixTranformations.transform(coverPanelMatrix[direction.ordinal()], direction);
		}
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

	private static void setRenderBounds(RenderBlocks renderblocks, float[][] rotated, ForgeDirection side) {
		renderblocks.setRenderBounds(
				rotated[0][0] + xOffsets[side.ordinal()],
				rotated[1][0] + yOffsets[side.ordinal()],
				rotated[2][0] + zOffsets[side.ordinal()],
				rotated[0][1] - xOffsets[side.ordinal()],
				rotated[1][1] - yOffsets[side.ordinal()],
				rotated[2][1] - zOffsets[side.ordinal()]);
	}
	
	
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		ISEGenericCable cable = (ISEGenericCable)tileEntity;
		
		for (ForgeDirection side: ForgeDirection.VALID_DIRECTIONS){
			ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side);
			if (coverPanel == null)
				continue;
			
			Block textuteBlock = coverPanel.getBlock();
			int textureMeta = coverPanel.getBlockMeta();
			
			if (!textuteBlock.canRenderInPass(renderPass))
				continue;

			
			FakeBlock fakeBlock = FakeBlock.instance;
			IIcon texture;
			IBlockAccess fakeBlockAccess = new FakeBlockAccess(world, side);
			fakeBlock.clearRenderMask();
			for (ForgeDirection textureSide: ForgeDirection.VALID_DIRECTIONS){
				texture = textuteBlock.getIcon(fakeBlockAccess, x, y, z, textureSide.ordinal());
				if (texture == null)
					texture = textuteBlock.getIcon(textureSide.ordinal(), textureMeta);	
				
				fakeBlock.setIcon(textureSide, texture);
				
				if (textureSide == ForgeDirection.UP)
					textureSide = ForgeDirection.UP;
				
				if (textureSide == side || textureSide == side.getOpposite()){
					fakeBlock.setRenderSide(textureSide, true);
				}else{
					ISECoverPanel cp = cable.getCoverPanelOnSide(textureSide);
					if (cp==null)
						fakeBlock.setRenderSide(textureSide, true);
				}
			}
			
			//BuildCraft Magic
			if (textuteBlock.getRenderType() == 31) {
				if ((textureMeta & 12) == 4) {
					renderer.uvRotateEast = 1;
					renderer.uvRotateWest = 1;
					renderer.uvRotateTop = 1;
					renderer.uvRotateBottom = 1;
				} else if ((textureMeta & 12) == 8) {
					renderer.uvRotateSouth = 1;
					renderer.uvRotateNorth = 1;
				}
			}

			fakeBlock.setColor(textuteBlock.getRenderColor(textureMeta));
			
			setRenderBounds(renderer, coverPanelMatrix[side.ordinal()], side);
			renderer.renderStandardBlock(fakeBlock, x, y, z);
			
			fakeBlock.setColor(0xFFFFFF);	
		}		
		
		Block blk = Block.getBlockFromName("gold_ore");
		IIcon icon = renderer.getBlockIcon(blk);
		//renderer.renderFaceYPos(blk, x, y, z, icon);
		//renderer.renderStandardBlock(Block.getBlockFromName("gold_ore"), x, y, z);

		
		double[][] myCube = createCubeVertexes(0.5,0.7,0.5);
		translateCoord(myCube,x,y,z);
		applyUV(myCube, icon);
		


		
		return false;	//Also make sure to only return true in renderWorldBlock() if you actually render something otherwise the game will crash.
	}
	
	public static void translateCoord(double[][] cubeVertexes, double x, double y, double z){
		cubeVertexes[0][0] += x;
		cubeVertexes[0][1] += y;
		cubeVertexes[0][2] += z;
		
		cubeVertexes[1][0] += x;
		cubeVertexes[1][1] += y;
		cubeVertexes[1][2] += z;
		
		cubeVertexes[2][0] += x;
		cubeVertexes[2][1] += y;
		cubeVertexes[2][2] += z;
		
		cubeVertexes[3][0] += x;
		cubeVertexes[3][1] += y;
		cubeVertexes[3][2] += z;
		
		cubeVertexes[4][0] += x;
		cubeVertexes[4][1] += y;
		cubeVertexes[4][2] += z;
		
		cubeVertexes[5][0] += x;
		cubeVertexes[5][1] += y;
		cubeVertexes[5][2] += z;
		
		cubeVertexes[6][0] += x;
		cubeVertexes[6][1] += y;
		cubeVertexes[6][2] += z;
		
		cubeVertexes[7][0] += x;
		cubeVertexes[7][1] += y;
		cubeVertexes[7][2] += z;
	}
	
	public static void applyUV(double[][] cubeVertexes, IIcon icon){
		Tessellator tessellator = Tessellator.instance;    
		
        double d3 = icon.getInterpolatedU(0 * 16.0D);
        double d4 = icon.getInterpolatedU(1 * 16.0D);
        double d5 = icon.getInterpolatedV(0 * 16.0D);
        double d6 = icon.getInterpolatedV(1 * 16.0D);
		
        double uMin = icon.getMinU();//3uMin
        double uMax = icon.getMaxU();//4uMax
        double vMin = icon.getMinV();//5vMin
        double vMax = icon.getMaxV();//6vMax
		
        //Down
		tessellator.addVertexWithUV(cubeVertexes[7][0], cubeVertexes[7][1], cubeVertexes[7][2], uMin, vMax);
        tessellator.addVertexWithUV(cubeVertexes[6][0], cubeVertexes[6][1], cubeVertexes[6][2], uMin, vMin);
        tessellator.addVertexWithUV(cubeVertexes[5][0], cubeVertexes[5][1], cubeVertexes[5][2], uMax, vMin);
        tessellator.addVertexWithUV(cubeVertexes[4][0], cubeVertexes[4][1], cubeVertexes[4][2], uMax, vMax);
        
		//Up
		tessellator.addVertexWithUV(cubeVertexes[0][0], cubeVertexes[0][1], cubeVertexes[0][2], uMax, vMax);
        tessellator.addVertexWithUV(cubeVertexes[1][0], cubeVertexes[1][1], cubeVertexes[1][2], uMax, vMin);
        tessellator.addVertexWithUV(cubeVertexes[2][0], cubeVertexes[2][1], cubeVertexes[2][2], uMin, vMin);
        tessellator.addVertexWithUV(cubeVertexes[3][0], cubeVertexes[3][1], cubeVertexes[3][2], uMin, vMax);
        
        
        //Zpos
        tessellator.addVertexWithUV(cubeVertexes[3][0], cubeVertexes[3][1], cubeVertexes[3][2], uMin, vMin);
        tessellator.addVertexWithUV(cubeVertexes[7][0], cubeVertexes[7][1], cubeVertexes[7][2], uMin, vMax);
        tessellator.addVertexWithUV(cubeVertexes[4][0], cubeVertexes[4][1], cubeVertexes[4][2], uMax, vMax);
        tessellator.addVertexWithUV(cubeVertexes[0][0], cubeVertexes[0][1], cubeVertexes[0][2], uMax, vMin);

	}
	
	public static double[][] createCubeVertexes(double maxX, double maxY, double maxZ){
		double[][] vertexes = new double[8][];
		double x = maxX / 2.0D;
		double z = maxZ / 2.0D;
		
		//Top
		vertexes[0] = new double[]{x,maxY,x};
		vertexes[1] = new double[]{x,maxY,-x};
		vertexes[2] = new double[]{-x,maxY,-x};
		vertexes[3] = new double[]{-x,maxY,x};
		
		//Bottom
		vertexes[4] = new double[]{x,0,x};
		vertexes[5] = new double[]{x,0,-x};
		vertexes[6] = new double[]{-x,0,-x};
		vertexes[7] = new double[]{-x,0,x};
        
        return vertexes;
	}
}
