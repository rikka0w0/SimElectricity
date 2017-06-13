package simelectricity.essential.cable.render;

import org.lwjgl.opengl.GL11;

import simelectricity.api.SEAPI;
import simelectricity.api.client.ITextureProvider;
import simelectricity.essential.api.ISECoverPanel;
import simelectricity.essential.api.ISEGenericCable;
import simelectricity.essential.cable.BlockCable;
import simelectricity.essential.utils.SERenderHelper;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTileCable extends TileEntitySpecialRenderer implements ITextureProvider {
	private static final ResourceLocation insulatorTexture = SERenderHelper.createResourceLocation("textures/cable/essentialCableInsulator.png");
	private static final ResourceLocation copperTexture = SERenderHelper.createResourceLocation("textures/cable/essentialCableCopper.png");
	
	
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tick) {
    	ISEGenericCable cable = (ISEGenericCable)tileEntity;
    	double thickness = BlockCable.thickness[tileEntity.getBlockMetadata()];
    	
        Tessellator t = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        //SEAPI.clientRender.renderCube(0.2, 1, 0.2, this, 0);
        GL11.glPopMatrix();
        
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        
        boolean rd,ru,rn,rs,rw,re;
        int numOfCon = 0;
        ForgeDirection towardDir = null;
        
        //Render branches
        for (ForgeDirection side: ForgeDirection.VALID_DIRECTIONS){
        	GL11.glPushMatrix();
        	SERenderHelper.rotateUpwardCoordSysTo(side);
        	if (cable.connectedOnSide(side)){
        		numOfCon++;
        		towardDir = side;
        		renderBranch(thickness, 0.5);
        	}
        	GL11.glPopMatrix();
        	

        	ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side);
        	if (coverPanel != null){
        		Block block = coverPanel.getBlock();
        		int meta = coverPanel.getBlockMeta();
        		
        		//GL11.glPushMatrix();
        		//SERenderHelper.rotateUpwardCoordSysTo(side);
        		//renderCoverPanel(block, meta, side);
            	//TODO : Render cover panel
        		//GL11.glPopMatrix();
        	}
        }
        
        //Render center
        if (numOfCon == 0){
        	for (ForgeDirection side: ForgeDirection.VALID_DIRECTIONS){
        		GL11.glPushMatrix();
        		SERenderHelper.rotateUpwardCoordSysTo(side);
        		renderCenterSegment(thickness);
        		GL11.glPopMatrix();
        	}
        }else if (numOfCon == 1){
        	GL11.glPushMatrix();
        	SERenderHelper.rotateUpwardCoordSysTo(towardDir.getOpposite());
        	GL11.glTranslated(0, -thickness, 0);
        	renderBranch(thickness, thickness * 3F/2F);
        	GL11.glPopMatrix();
        }else{
        	for (ForgeDirection side: ForgeDirection.VALID_DIRECTIONS){
        		if (!cable.connectedOnSide(side)){
        			GL11.glPushMatrix();
        			SERenderHelper.rotateUpwardCoordSysTo(side);
        			renderCenterSegment(thickness);
        			GL11.glPopMatrix();
        		}
        	}
        }
        
        GL11.glPopMatrix();
	}

    /*
    private void renderCoverPanel(Block block, int meta, ForgeDirection side){
		IIcon icon = block.getIcon(side.UP.ordinal(), meta);
		Tessellator t = Tessellator.instance;
		
		TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
		bindTexture(texturemanager.getResourceLocation(0)); 
		
        t.startDrawingQuads();
        t.setNormal(0, 1, 0);
	    t.addVertexWithUV(-0.5, 0.5, -0.5, icon.getMinU(), icon.getMinV());
	    t.addVertexWithUV(-0.5, 0.5, 0.5, icon.getMinU(), icon.getMaxV());
	    t.addVertexWithUV(0.5, 0.5, 0.5, icon.getMaxU(), icon.getMaxV());
	    t.addVertexWithUV(0.5, 0.5, -0.5, icon.getMaxU(), icon.getMinV());
		t.draw();
    }*/
    
    /**
     * Facing upwards, 2-D square
     * <p/>
     * Center: (0,thickness/2,0)
     */
    private void renderCenterSegment(double thickness){
    	Tessellator t = Tessellator.instance;
    	
    	double 	bound = thickness/2;
    	
        bindTexture(insulatorTexture);
        t.startDrawingQuads();
        t.setNormal(0, 1, 0);
        t.addVertexWithUV(-bound, bound, bound, 0, 0);
        t.addVertexWithUV(bound, bound, bound, 1, 0);
        t.addVertexWithUV(bound, bound, -bound, 1, 1);
        t.addVertexWithUV(-bound, bound, -bound, 0, 1);
        t.draw();
    }

    
    /**
     * Facing upwards, up - copper, down - nothing, other - insulator
     * <p/>
     * Center: (0,thickness/2,0), height:  maxY - thickness/2
     */
    private void renderBranch(double thickness, double maxY){
    	Tessellator t = Tessellator.instance;
    	
    	double 	bound = thickness/2;
    	
        bindTexture(insulatorTexture);
        t.startDrawingQuads();
        t.setNormal(-1, 0, 0);
        t.addVertexWithUV(-bound, bound, -bound, 0, 1);
        t.addVertexWithUV(-bound, bound, bound, 1, 1);
        t.addVertexWithUV(-bound, maxY, bound, 1, 0);
        t.addVertexWithUV(-bound, maxY, -bound, 0, 0);
        t.draw();

        bindTexture(insulatorTexture);
        t.startDrawingQuads();
        t.setNormal(1, 0, 0);
        t.addVertexWithUV(bound, maxY, -bound, 1, 0);
        t.addVertexWithUV(bound, maxY, bound, 0, 0);
        t.addVertexWithUV(bound, bound, bound, 0, 1);
        t.addVertexWithUV(bound, bound, -bound, 1, 1);
        t.draw();

        bindTexture(insulatorTexture);
        t.startDrawingQuads();
        t.setNormal(0, 0, -1);
        t.addVertexWithUV(-bound, maxY, -bound, 1, 0);
        t.addVertexWithUV(bound, maxY, -bound, 0, 0);
        t.addVertexWithUV(bound, bound, -bound, 0, 1);
        t.addVertexWithUV(-bound, bound, -bound, 1, 1);
        t.draw();

        bindTexture(insulatorTexture);
        t.startDrawingQuads();
        t.setNormal(0, 0, 1);
        t.addVertexWithUV(-bound, maxY, bound, 0, 0);
        t.addVertexWithUV(-bound, bound, bound, 0, 1);
        t.addVertexWithUV(bound, bound, bound, 1, 1);
        t.addVertexWithUV(bound, maxY, bound, 1, 0);
        t.draw();
        
        
        bindTexture(copperTexture);
        t.startDrawingQuads();
        t.setNormal(0, 1, 0);
        t.addVertexWithUV(-bound, maxY, bound, 0, 0);
        t.addVertexWithUV(bound, maxY, bound, 1, 0);
        t.addVertexWithUV(bound, maxY, -bound, 1, 1);
        t.addVertexWithUV(-bound, maxY, -bound, 0, 1);
        t.draw();
    }

	@Override
	public void bindTexture(int index, int side) {
		bindTexture(copperTexture);
	}
}
