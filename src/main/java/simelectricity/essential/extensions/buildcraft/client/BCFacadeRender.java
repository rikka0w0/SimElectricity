package simelectricity.essential.extensions.buildcraft.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simelectricity.essential.api.ISECoverPanel;
import simelectricity.essential.api.ISECoverPanelRender;
import simelectricity.essential.api.ISEGenericCable;
import simelectricity.essential.extensions.buildcraft.BCFacadePanel;
import simelectricity.essential.utils.MatrixTranformations;
import simelectricity.essential.utils.client.SERenderHelper;

/**
 * Part of this source code comes from BuildCraft
 * <p/>
 * @author Rikka0_0
 */
@SideOnly(Side.CLIENT)
public class BCFacadeRender implements ISECoverPanelRender<BCFacadePanel>{
	public static ISECoverPanelRender instance;
	
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
			coverPanelMatrix[direction.ordinal()][1][1] = (float) ISECoverPanel.thickness;
			// Z START - END
			coverPanelMatrix[direction.ordinal()][2][0] = 0.0F;
			coverPanelMatrix[direction.ordinal()][2][1] = 1.0F;
			MatrixTranformations.transform(coverPanelMatrix[direction.ordinal()], direction);
		}
	}

	public BCFacadeRender(){
		MinecraftForge.EVENT_BUS.register(this);
		this.instance = this;
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
	@SideOnly(Side.CLIENT)
	public void renderCoverPanel(IBlockAccess world, int x, int y, int z,
			RenderBlocks renderer, int renderPass, ISEGenericCable cable,
			BCFacadePanel coverPanel, ForgeDirection side) {
		Block textuteBlock = coverPanel.getBlock();
		int textureMeta = coverPanel.getBlockMeta();
		
		if (!textuteBlock.canRenderInPass(renderPass))
			return;
		
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
		
		Block block = world.getBlock(x, y, z);
		int lightValue = block.getMixedBrightnessForBlock(world, x, y, z);
		double[][] cube = SERenderHelper.createCubeVertexes(0.1, 0.5 - ISECoverPanel.thickness, 0.1);
		SERenderHelper.rotateCubeToDirection(cube, side);
		SERenderHelper.translateCoord(cube, x+0.5, y+0.5, z+0.5);
		SERenderHelper.addCubeToTessellator(cube, textureArray, lightValue);
	}
	
	///////////////////////////////////
	/// Load texture for models
	///////////////////////////////////
	private static IIcon supportTexture;
	private static IIcon[] textureArray;
	@SubscribeEvent
	public void eventHandler(TextureStitchEvent.Pre event){
		if (event.map.getTextureType() == 0){
			supportTexture = event.map.registerIcon("sime_essential:coverpanel/support");
			textureArray = new IIcon[]{null, null, supportTexture, supportTexture, supportTexture, supportTexture};
		}
	}
}
