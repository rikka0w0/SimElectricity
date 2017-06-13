package simelectricity.essential.cable.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import simelectricity.essential.api.ISECoverPanel;
import simelectricity.essential.api.ISEGenericCable;
import simelectricity.essential.cable.BlockCable;
import simelectricity.essential.cable.CoverPanel;
import simelectricity.essential.utils.MatrixTranformations;

/**
 * Part of this source code is from BuildCraft
 * <p/>
 * @author Rikka0_0
 */
public class RenderHelperCoverPanel {
	private static final float[] xOffsets = new float[6];
	private static final float[] yOffsets = new float[6];
	private static final float[] zOffsets = new float[6];
	
	private static final float[][][] coverPanelMatrix = new float[6][3][2];
	
	static{
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
			coverPanelMatrix[direction.ordinal()][1][1] = (float) CoverPanel.thickness;
			// Z START - END
			coverPanelMatrix[direction.ordinal()][2][0] = 0.0F;
			coverPanelMatrix[direction.ordinal()][2][1] = 1.0F;
			MatrixTranformations.transform(coverPanelMatrix[direction.ordinal()], direction);
		}
	}
	
	public static void renderCoverPanel(IBlockAccess world, int x, int y, int z, RenderBlocks renderer, int renderPass, ISEGenericCable cable){		
		/////////////////////////////
		/// Render cover panels
		/////////////////////////////
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
}
