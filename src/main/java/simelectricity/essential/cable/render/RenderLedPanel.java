package simelectricity.essential.cable.render;

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
import simelectricity.essential.cable.LedPanel;
import simelectricity.essential.utils.client.SERenderHelper;

public class RenderLedPanel implements ISECoverPanelRender<LedPanel>{
	public static ISECoverPanelRender instance;
	
	public RenderLedPanel(){
		MinecraftForge.EVENT_BUS.register(this);
		this.instance = this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderCoverPanel(IBlockAccess world, int x, int y, int z,
			RenderBlocks renderer, int renderPass, ISEGenericCable cable,
			LedPanel coverPanel, ForgeDirection side) {
		
		Block block = world.getBlock(x, y, z);
		int lightValue = block.getMixedBrightnessForBlock(world, x, y, z);
		double[][] cube = SERenderHelper.createCubeVertexes(0.1, 0.5 - ISECoverPanel.thickness, 0.1);
		SERenderHelper.rotateCubeToDirection(cube, side);
		SERenderHelper.translateCoord(cube, x+0.5, y+0.5, z+0.5);
		SERenderHelper.addCubeToTessellator(cube, textureArray, lightValue);
		
		IIcon[] panelTextureArray = new IIcon[] {panelTexture, panelTexture, panelTexture, panelTexture, panelTexture, panelTexture};
		for (ForgeDirection textureSide: ForgeDirection.VALID_DIRECTIONS){
			if (textureSide == side || textureSide == side.getOpposite()){
				panelTextureArray[textureSide.ordinal()] = panelTexture;
			}else{
				ISECoverPanel cp = cable.getCoverPanelOnSide(textureSide);
				if (cp!=null)
					panelTextureArray[textureSide.ordinal()] = null;
			}
		}
		
		
		switch (side){
		case DOWN:
			cube = SERenderHelper.createCubeVertexes(1, ISECoverPanel.thickness, 1);
			SERenderHelper.translateCoord(cube, x+0.5, y, z+0.5);
			break;
		case UP:
			cube = SERenderHelper.createCubeVertexes(1, ISECoverPanel.thickness, 1);
			SERenderHelper.translateCoord(cube, x+0.5, y + 1 - ISECoverPanel.thickness, z+0.5);
			break;
		case NORTH:
			cube = SERenderHelper.createCubeVertexes(1, 1, ISECoverPanel.thickness);
			SERenderHelper.translateCoord(cube, x+0.5, y, z + ISECoverPanel.thickness/2);
			break;		
		case SOUTH:
			cube = SERenderHelper.createCubeVertexes(1, 1, ISECoverPanel.thickness);
			SERenderHelper.translateCoord(cube, x+0.5, y, z + 1 - ISECoverPanel.thickness/2);
			break;		
		case WEST:
			cube = SERenderHelper.createCubeVertexes(ISECoverPanel.thickness, 1, 1);
			SERenderHelper.translateCoord(cube, x + ISECoverPanel.thickness/2, y , z+0.5);
			break;
		case EAST:
			cube = SERenderHelper.createCubeVertexes(ISECoverPanel.thickness, 1, 1);
			SERenderHelper.translateCoord(cube, x + 1 - ISECoverPanel.thickness/2, y , z+0.5);
			break;
		default:
			cube = null;
			break;
		}

		if (cube != null)
			SERenderHelper.addCubeToTessellator(cube, panelTextureArray, lightValue);
	}
	
	///////////////////////////////////
	/// Load texture for models
	///////////////////////////////////
	private static IIcon supportTexture;
	private static IIcon[] textureArray;
	private static IIcon panelTexture;
	@SubscribeEvent
	public void eventHandler(TextureStitchEvent.Pre event){
		if (event.map.getTextureType() == 0){
			supportTexture = event.map.registerIcon("sime_essential:coverpanel/support");
			textureArray = new IIcon[]{null, null, supportTexture, supportTexture, supportTexture, supportTexture};
			panelTexture = event.map.registerIcon("sime_essential:coverpanel/ledpanel");
		}
	}
}
