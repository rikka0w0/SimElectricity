package simelectricity.essential.grid.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import simelectricity.essential.api.ISETransmissionTower;

public class TileRenderTranmissionTowerBase extends TileEntitySpecialRenderer implements ITextureProvider{
	protected double[] from1, to1;
	protected double[] fixedfrom1, fixedto1;
	protected double[] angle1;
	
	protected double[] from2, to2;
	protected double[] angle2;
	protected double[] fixedfrom2,fixedto2;
	
	protected boolean render1, render2;
	
	protected double rotation;
	
	@Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
		TransmissionTowerRenderHelper helper = ((ISETransmissionTower) tileEntity).getRenderHelper();
		
		if (helper == null)
			return;
		
		render1 = helper.render1();
		from1 = helper.from1();
		to1 = helper.to1();
		fixedfrom1 = helper.fixedfrom1();
		fixedto1 = helper.fixedto1();
		angle1 = helper.angle1();
		
		render2 = helper.render2();
		from2 = helper.from2();
		to2 = helper.to2();
		angle2 = helper.angle2();
	    fixedfrom2 = helper.fixedfrom2();
	    fixedto2 = helper.fixedto2();
		
	    rotation = helper.getRotation();

	    
        //Cable rendering   
        GL11.glPushMatrix();
        GL11.glTranslated(x-tileEntity.xCoord, y-tileEntity.yCoord, z-tileEntity.zCoord);
        if (helper.render1()){
        	renderCableAndInsulator(from1, to1, fixedfrom1, fixedto1, angle1, tileEntity.getBlockMetadata());
        }
        if (helper.render2()){
        	renderCableAndInsulator(from2, to2, fixedfrom2, fixedto2, angle2, tileEntity.getBlockMetadata());
        }
        GL11.glPopMatrix();
	}
	
	private void renderCableAndInsulator(double[] from, double[] to, double[] fixedfrom, double[] fixedto, double[] angle, int meta){
		for (int i=0; i<3; i++){        
	        //Render cable
	        GL11.glPushMatrix();
	        GL11.glTranslated(fixedfrom[3*i], fixedfrom[3*i+1],fixedfrom[3*i+2]);
	        CableGLRender.renderHalfParabolicCable(fixedfrom[3*i], fixedfrom[3*i+1],fixedfrom[3*i+2], fixedto[3*i], fixedto[3*i+1],fixedto[3*i+2], 0.15, 3, this, 1);
	        GL11.glPopMatrix();
		}
	}
	
	@Override
	public void bindTexture(int index, int side) {
		bindTexture(new ResourceLocation("sime_essential", "textures/blocks/transmission/hv_cable.png"));
	}
}
