package simElectricity.Templates.Client.Render;

import org.lwjgl.opengl.GL11;

import simElectricity.API.SEAPI;
import simElectricity.API.Client.ITextureProvider;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RenderTransmissionTower extends TileEntitySpecialRenderer implements ITextureProvider{
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
    
    private void getRotatedXZ(double[] param, double rotation, int x, int z){	//ret: x,z, real MC coord
    	double lrx = param[0];
    	double lrz = param[1];
		param[0] = lrz * Math.sin(rotation/180*Math.PI) + lrx * Math.cos(rotation/180*Math.PI) + 0.5F + x;
		param[1] = lrz * Math.cos(rotation/180*Math.PI) - lrx * Math.sin(rotation/180*Math.PI) + 0.5F + z;
    }
    
    private void transformCoord(double[] param, double rotation, int x, int z){	//ret: x,z, real MC coord
    	for (int i=0; i<9; i+=3){
    		double lrx = param[i];
    		double lrz = param[i+2];
    		param[i] = lrz * Math.sin(rotation/180*Math.PI) + lrx * Math.cos(rotation/180*Math.PI) + 0.5F + x;
    		param[i+2] = lrz * Math.cos(rotation/180*Math.PI) - lrx * Math.sin(rotation/180*Math.PI) + 0.5F + z;
    	}

    }
    
    private double distanceOf(double[] Start, double[] End) {
        return Math.sqrt((Start[0] - End[0])*(Start[0] - End[0]) + (Start[1] - End[1])*(Start[1] - End[1]));
    }
    
    private void findConnection(TileEntity tileEntity, TileEntity neighbor, double[] from, double[] to){    	
    	ITransmissionTower curTw = (ITransmissionTower) tileEntity;
    	ITransmissionTower neighborTw = (ITransmissionTower) neighbor;
    	
		float curRotation = curTw.getRotation()*45 - 90;
		double[] curInsulatorPositionArray = curTw.getInsulatorPositionArray();		
		
		float neighborRotation = neighborTw.getRotation()*45 - 90;
		double[] neighborInsulatorPositionArray = neighborTw.getInsulatorPositionArray();		
		
		
		
		double[] curInsulatorXZ1 = new double[] {curInsulatorPositionArray[3], curInsulatorPositionArray[5]};
		getRotatedXZ(curInsulatorXZ1, curRotation, tileEntity.xCoord, tileEntity.zCoord);
		
		double[] neighborInsulatorXZ1 = new double[] {neighborInsulatorPositionArray[3], neighborInsulatorPositionArray[5]};
		getRotatedXZ(neighborInsulatorXZ1, neighborRotation, neighbor.xCoord, neighbor.zCoord);
		
		double[] curInsulatorXZ2 = null;
		if (curInsulatorPositionArray.length > 9){
			curInsulatorXZ2 = new double[] {curInsulatorPositionArray[12], curInsulatorPositionArray[14]};
			getRotatedXZ(curInsulatorXZ2, curRotation, tileEntity.xCoord, tileEntity.zCoord);
		}
		
		double[] neighborInsulatorXZ2 = null;
		if (neighborInsulatorPositionArray.length > 9){
			neighborInsulatorXZ2 = new double[] {neighborInsulatorPositionArray[12], neighborInsulatorPositionArray[14]};
			getRotatedXZ(neighborInsulatorXZ2, neighborRotation, neighbor.xCoord, neighbor.zCoord);
		}
		
		
		
		
		
		if (curInsulatorXZ2 == null && neighborInsulatorXZ2 == null){
			for (int i=0; i<9; i++){
				from[i] = curInsulatorPositionArray[i];
				to[i] = neighborInsulatorPositionArray[i];
			}
		}
		else if (curInsulatorXZ2 != null && neighborInsulatorXZ2 == null){
			double dc1n1 = distanceOf(curInsulatorXZ1, neighborInsulatorXZ1);
			double dc2n1 = distanceOf(curInsulatorXZ2, neighborInsulatorXZ1);
			
			if (dc2n1<dc1n1){
				for (int i=0; i<9; i++){
					from[i] = curInsulatorPositionArray[i+9];
					to[i] = neighborInsulatorPositionArray[i];
				}
			}else{
				for (int i=0; i<9; i++){
					from[i] = curInsulatorPositionArray[i];
					to[i] = neighborInsulatorPositionArray[i];
				}
			}
		}
		else if (curInsulatorXZ2 == null && neighborInsulatorXZ2 != null){
			double dc1n1 = distanceOf(curInsulatorXZ1, neighborInsulatorXZ1);
			double dc1n2 = distanceOf(curInsulatorXZ1, neighborInsulatorXZ2);
			
			if (dc1n2<dc1n1){
				for (int i=0; i<9; i++){
					from[i] = curInsulatorPositionArray[i];
					to[i] = neighborInsulatorPositionArray[i+9];
				}
			}else{
				for (int i=0; i<9; i++){
					from[i] = curInsulatorPositionArray[i];
					to[i] = neighborInsulatorPositionArray[i];
				}
			}
		}
		else if (curInsulatorXZ2 != null && neighborInsulatorXZ2 != null){
			double dc1n1 = distanceOf(curInsulatorXZ1, neighborInsulatorXZ1);
			double dc1n2 = distanceOf(curInsulatorXZ1, neighborInsulatorXZ2);
			double dc2n1 = distanceOf(curInsulatorXZ2, neighborInsulatorXZ1);
			double dc2n2 = distanceOf(curInsulatorXZ2, neighborInsulatorXZ2);
					
			for (int i=0; i<9; i++){
				from[i] = curInsulatorPositionArray[i];
				to[i] = neighborInsulatorPositionArray[i];
			}
			if (dc1n2<dc1n1){
				dc1n1 = dc1n2;
				for (int i=0; i<9; i++){
					from[i] = curInsulatorPositionArray[i];
					to[i] = neighborInsulatorPositionArray[i+9];
				}
			}
			if (dc2n1<dc1n1){
				dc1n1 = dc2n1;
				for (int i=0; i<9; i++){
					from[i] = curInsulatorPositionArray[i+9];
					to[i] = neighborInsulatorPositionArray[i];
				}
			}
			if (dc2n2<dc1n1){
				for (int i=0; i<9; i++){
					from[i] = curInsulatorPositionArray[i+9];
					to[i] = neighborInsulatorPositionArray[i+9];
				}
			}
		}
		
		//Transform to 'Real' MC coordinates
		transformCoord(from, curRotation, tileEntity.xCoord, tileEntity.zCoord);
		transformCoord(to, neighborRotation, neighbor.xCoord, neighbor.zCoord);
		from[1] += tileEntity.yCoord;
		from[4] += tileEntity.yCoord;
		from[7] += tileEntity.yCoord;
		to[1] += neighbor.yCoord;
		to[4] += neighbor.yCoord;
		to[7] += neighbor.yCoord;
		
		//Swap if intersect
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
    
    private double calcInitSlope(double xStart, double yStart, double zStart, double xEnd, double yEnd, double zEnd, double tension){
    	double length = SEAPI.clientRender.distanceOf(xStart, yStart, zStart, xEnd, yEnd, zEnd);
    	double b = 4 * tension / length;
    	double a = -b / length;
    	return -Math.atan(2*a+b);
    }
    
    //I: from I: to O: fixed, angle
    private void fixConnectionPoints(double[] from, double[] to, double[] angles, double[] fixedfrom, double insulatorLength, double tension){
        angles[0] = calcInitSlope(from[0], from[1], from[2], to[0], to[1], to[2], tension)*1.3;
        double lcos = insulatorLength * Math.cos(angles[0]);
        double atan = Math.atan2(to[0] - from[0], from[2] - to[2]);        
        fixedfrom[0] = from[0] + lcos * Math.sin(atan);
        fixedfrom[1] = from[1] + insulatorLength * Math.sin(angles[0]);
        fixedfrom[2] = from[2] - lcos * Math.cos(atan);
        
        angles[1] = calcInitSlope(from[3], from[4], from[5], to[3], to[4], to[5], tension)*1.3;
        lcos = insulatorLength * Math.cos(angles[1]);
        atan = Math.atan2(to[3] - from[3], from[5] - to[5]);
        fixedfrom[3] = from[3] + lcos * Math.sin(atan);
        fixedfrom[4] = from[4] + insulatorLength * Math.sin(angles[1]);
        fixedfrom[5] = from[5] - lcos * Math.cos(atan);
        
        angles[2] = calcInitSlope(from[6], from[7], from[8], to[6], to[7], to[8], tension)*1.3;
        lcos = insulatorLength * Math.cos(angles[2]);
        atan = Math.atan2(to[6] - from[6], from[8] - to[8]);
        fixedfrom[6] = from[6] + lcos * Math.sin(atan);
        fixedfrom[7] = from[7] + insulatorLength * Math.sin(angles[2]);
        fixedfrom[8] = from[8] - lcos * Math.cos(atan);
    }
    
  
    
    /*{initSlopeAngle, newCoords}
    private double[] fixConnectionPoint(double xStart, double yStart, double zStart, double xEnd, double yEnd, double zEnd, double insulatorLength, double tension){
        double initSlopeAngle = calcInitSlope(xStart, yStart, zStart, xEnd, yEnd, zEnd, tension)*1.3;
        double lcos = insulatorLength * Math.cos(initSlopeAngle);
        double atan = Math.atan2(xEnd - xStart, zStart - zEnd);
        double mx = lcos * Math.sin(atan);
        double my = insulatorLength * Math.sin(initSlopeAngle);
        double mz = -lcos * Math.cos(atan);
        
        return new double[] {initSlopeAngle, xStart+mx, yStart+my, zStart+mz};
    }
    */
    
	double[] from1 = new double[9], to1 = new double[9];
	double[] fixedfrom1 = new double[9], fixedto1 = new double[9];
	double[] angle1 = new double[3];
	
	double[] from2 = new double[9], to2 = new double[9];
	double[] angle2 = new double[3];
    double[] fixedfrom2 = new double[9],fixedto2 = new double[9];
    
    double[] dummyangle = new double[3];
    
	@Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
		ITransmissionTower tw = (ITransmissionTower) tileEntity;
		
		float rotation = tw.getRotation()*45 - 90;
        
		int[] neighborCoords = tw.getNeighborCoordArray();
		TileEntity neighbor1 = tileEntity.getWorldObj().getTileEntity(neighborCoords[0], neighborCoords[1], neighborCoords[2]);
		TileEntity neighbor2 = tileEntity.getWorldObj().getTileEntity(neighborCoords[3], neighborCoords[4], neighborCoords[5]);
		
		if (neighbor1 != null){
			findConnection(tileEntity, neighbor1, from1, to1);
			
			if (tw.getInsulatorPositionArray().length > 9){
				fixConnectionPoints(from1, to1, angle1, fixedfrom1, 2, 3);
			}else{
				for (int i=0; i<9; i++)
					fixedfrom1[i] = from1[i];
			}
			
			if (((ITransmissionTower) neighbor1).getInsulatorPositionArray().length > 9)
				fixConnectionPoints(to1, from1, dummyangle, fixedto1, 2, 3);
			else{
				for (int i=0; i<9; i++)
					fixedto1[i] = to1[i];
			}
		}
			
		if (neighbor2 != null){
			from2 = new double[9];
			to2 = new double[9];
			findConnection(tileEntity, neighbor2, from2, to2);
			
			if (tw.getInsulatorPositionArray().length > 9){
				fixConnectionPoints(from2, to2, angle2, fixedfrom2, 2, 3);
			}else{
				for (int i=0; i<9; i++)
					fixedfrom2[i] = from2[i];
			}
			
			if (((ITransmissionTower) neighbor2).getInsulatorPositionArray().length > 9)
				fixConnectionPoints(to2, from2, dummyangle, fixedto2, 2, 3);
			else{
				for (int i=0; i<9; i++)
					fixedto2[i] = to2[i];				
			}

		}
		
		
    	//Tower rendering
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glTranslated(0.5, 0, 0.5);
        GL11.glRotatef(tw.getRotation()*45 - 90, 0F, 1F, 0F);

        //Debugging purpose, indicates the direction
        GL11.glPushMatrix();
        SEAPI.clientRender.renderCable(0, 0, 0, 1, 0, 0, 0.1, this, 2);
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
        renderTower(tileEntity.getBlockMetadata()); 
        GL11.glPopMatrix();
               
        GL11.glPopMatrix();
        //End of tower rendering
        
        
        
        
        
        //Cable rendering   
        GL11.glPushMatrix();
        GL11.glTranslated(x-tileEntity.xCoord, y-tileEntity.yCoord, z-tileEntity.zCoord);
        if (neighbor1 != null){
            double insulatorLength = 2;

            for (int i=0; i<3; i++){
                GL11.glPushMatrix();
                GL11.glTranslated(from1[3*i], from1[3*i+1],from1[3*i+2]);
                SEAPI.clientRender.p2pRotation(from1[3*i],from1[3*i+1],from1[3*i+2], to1[3*i],from1[3*i+1],to1[3*i+2]);
                GL11.glRotated(angle1[1]/Math.PI*180, 0, 0, 1);
                renderInsulator(tileEntity.getBlockMetadata(), (float) insulatorLength);
                GL11.glPopMatrix();
                
                GL11.glPushMatrix();
                GL11.glTranslated(fixedfrom1[3*i], fixedfrom1[3*i+1],fixedfrom1[3*i+2]);
                SEAPI.clientRender.renderHalfParabolicCable(fixedfrom1[3*i], fixedfrom1[3*i+1],fixedfrom1[3*i+2], fixedto1[3*i], fixedto1[3*i+1],fixedto1[3*i+2], 0.15, 3, this, 1);
                GL11.glPopMatrix();
                
            }
        }
        if (neighbor2 != null){
            double insulatorLength = 2;

             for (int i=0; i<3; i++){
                GL11.glPushMatrix();
                GL11.glTranslated(from2[3*i], from2[3*i+1],from2[3*i+2]);
                SEAPI.clientRender.p2pRotation(from2[3*i],from2[3*i+1],from2[3*i+2], to2[3*i],from2[3*i+1],to2[3*i+2]);
                GL11.glRotated(angle2[1]/Math.PI*180, 0, 0, 1);
                renderInsulator(tileEntity.getBlockMetadata(), (float) insulatorLength);
                GL11.glPopMatrix();
                
                GL11.glPushMatrix();
                GL11.glTranslated(fixedfrom2[3*i], fixedfrom2[3*i+1],fixedfrom2[3*i+2]);
                SEAPI.clientRender.renderHalfParabolicCable(fixedfrom2[3*i], fixedfrom2[3*i+1],fixedfrom2[3*i+2], fixedto2[3*i], fixedto2[3*i+1],fixedto2[3*i+2], 0.15, 3, this, 1);
                GL11.glPopMatrix();
                
            }
            
            
            
        }
        GL11.glPopMatrix();
	}
    
	public void renderInsulator(int meta, float length){
		switch (meta){
		case 0:
			Models.renderInsulator(length, this, 0 ,2);
			break;
		case 1:
			break;
		default:
			break;
		}
		
	}
	
	public void renderTower(int meta){
		switch (meta){
		case 0:
			Models.renderTower0(this);
			break;
		case 1:
			GL11.glPushMatrix();
			GL11.glTranslated(0,25,4);
			GL11.glRotated(180,0,0,1);
			Models.renderInsulator(2, this, 0 ,2);
			GL11.glPopMatrix();
			
			GL11.glPushMatrix();
			GL11.glTranslated(0,18,4.5);
			GL11.glRotated(180,0,0,1);
			Models.renderInsulator(2, this, 0 ,2);
			GL11.glPopMatrix();
			
			GL11.glPushMatrix();
			GL11.glTranslated(0,18,-4.5);
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
