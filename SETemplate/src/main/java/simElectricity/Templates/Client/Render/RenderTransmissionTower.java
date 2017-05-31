package simElectricity.Templates.Client.Render;

import org.lwjgl.opengl.GL11;

import simElectricity.API.SEAPI;
import simElectricity.API.Client.ITransmissionTower;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RenderTransmissionTower extends RenderTranmissionTowerBase{
	double[] 	from = new double[9],
				to = new double[9];
    private static void swapIfIntersect(double[] from, double[] to){
		double m1 = (from[0]-to[0])/(from[2]-to[2]);
		double k1 = from[0] - from[2] * m1;
		double m2 = (from[6]-to[6])/(from[8]-to[8]);
		double k2 = from[6] - from[8] * m2;
		
		double zc = (k2-k1)/(m1-m2);
		double zx = m1*zc + k1;
		
		if ((from[0]> zx && zx > to[0]) || (from[0]<zx && zx<to[0])){
			m1 = from[6];
			k1 = from[7];
			m2 = from[8];
			from[6] = from[0];
			from[7] = from[1];
			from[8] = from[2];
			from[0] = m1;
			from[1] = k1;
			from[2] = m2;
		}
    }
	
	
    @Override
    public void bindTexture(int index, int side) {
        switch (index) {
            case 1:
                bindTexture(new ResourceLocation("simelectricity", "textures/blocks/Wiring/CopperCable_Thin_Side.png"));
                return;
            case 2:
                bindTexture(new ResourceLocation("simelectricity", "textures/render/HvInsulator.png"));
                return;
            default:
                bindTexture(new ResourceLocation("simelectricity", "textures/blocks/AdjustableResistor_Top.png"));
        }
    }

    
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
    	ITransmissionTower tw = ((ITransmissionTower) tileEntity);
    	super.renderTileEntityAt(tileEntity, x, y, z, f);
    	
    	if (tileEntity.getBlockMetadata() == 0 && render1 && render2){    		
    		for (int i=0; i<9; i++){
    			from[i] = fixedfrom1[i];
    			to[i] = fixedfrom2[i];
    		}
    		
    		swapIfIntersect(from,to);
            GL11.glPushMatrix();
            GL11.glTranslated(x-tileEntity.xCoord, y-tileEntity.yCoord, z-tileEntity.zCoord);
            GL11.glTranslated(from[0], from[1],from[2]);
	        SEAPI.clientRender.renderParabolicCable(from[0], from[1],from[2], to[0], to[1],to[2], 0.15, 2, this, 1);
            GL11.glPopMatrix();
            
            GL11.glPushMatrix();
            GL11.glTranslated(x-tileEntity.xCoord, y-tileEntity.yCoord, z-tileEntity.zCoord);
            GL11.glTranslated(from[6], from[7],from[8]);
	        SEAPI.clientRender.renderParabolicCable(from[6], from[7],from[8], to[6], to[7],to[8], 0.15, 2, this, 1);
            GL11.glPopMatrix();
            
            
            
            double[] insulatorPositionArray = tw.getInsulatorPositionArray();
           
            
            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);
            GL11.glTranslated(0.5, 0, 0.5);
            GL11.glRotated(tw.getRenderHelper().getRotation(), 0, 1, 0);
        	GL11.glTranslated(0, 25, 3.95);
            GL11.glRotated(180, 0, 0, 1);
            Models.renderInsulator(2, this, 0 ,2);
            GL11.glPopMatrix();
            
            GL11.glPushMatrix();
            GL11.glTranslated(x-tileEntity.xCoord, y-tileEntity.yCoord, z-tileEntity.zCoord);
            GL11.glTranslated(fixedfrom1[3], fixedfrom1[4],fixedfrom1[5]);
            SEAPI.clientRender.renderParabolicCable(fixedfrom1[3], fixedfrom1[4],fixedfrom1[5],
            		3.95 * Math.sin(rotation/180*Math.PI) + 0.5F + tileEntity.xCoord,
            		tileEntity.yCoord + 23,
            		3.95 * Math.cos(rotation/180*Math.PI) + 0.5F + tileEntity.zCoord,
            		0.15, 2, this, 1);
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glTranslated(x-tileEntity.xCoord, y-tileEntity.yCoord, z-tileEntity.zCoord);
            GL11.glTranslated(fixedfrom2[3], fixedfrom2[4],fixedfrom2[5]);
            SEAPI.clientRender.renderParabolicCable(fixedfrom2[3], fixedfrom2[4],fixedfrom2[5],
            		3.95 * Math.sin(rotation/180*Math.PI) + 0.5F + tileEntity.xCoord,
            		tileEntity.yCoord + 23,
            		3.95 * Math.cos(rotation/180*Math.PI) + 0.5F + tileEntity.zCoord,
            		0.15, 2, this, 1);
            GL11.glPopMatrix();
    	}
    }
	
	@Override
	public void renderInsulator(int meta){
		switch (meta){
		case 0:
			Models.renderInsulator(2, this, 0 ,2);
			break;
		case 1:
			break;
		default:
			break;
		}
		
	}
	
	@Override
	public void renderTower(int meta){
		switch (meta){
		case 0:
			Models.renderTower0(this);
			break;
		case 1:
			GL11.glPushMatrix();
			GL11.glTranslated(0,25,3.95);
			GL11.glRotated(180,0,0,1);
			Models.renderInsulator(2, this, 0 ,2);
			GL11.glPopMatrix();
			
			GL11.glPushMatrix();
			GL11.glTranslated(0,18,4.9);
			GL11.glRotated(180,0,0,1);
			Models.renderInsulator(2, this, 0 ,2);
			GL11.glPopMatrix();
			
			GL11.glPushMatrix();
			GL11.glTranslated(0,18,-4.9);
			GL11.glRotated(180,0,0,1);
			Models.renderInsulator(2, this, 0 ,2);
			GL11.glPopMatrix();
			
			Models.renderTower0(this);
			break;
		default:
			break;
		}
		
	}
}
