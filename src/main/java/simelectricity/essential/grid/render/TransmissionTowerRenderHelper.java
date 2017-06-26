package simelectricity.essential.grid.render;

import net.minecraft.tileentity.TileEntity;
import simelectricity.essential.grid.ISETransmissionTower;
import simelectricity.essential.utils.SEMathHelper;

public class TransmissionTowerRenderHelper{
	private TileEntity te;
	private double insulatorLength;
	private double[] insulatorPositionArray;
	
	private double rotation;
	
	private double[] from1 = new double[9], to1 = new double[9];
	private double[] fixedfrom1 = new double[9], fixedto1 = new double[9];
	private double[] angle1 = new double[3];
	
	private double[] from2 = new double[9], to2 = new double[9];
	private double[] angle2 = new double[3];
	private double[] fixedfrom2 = new double[9],fixedto2 = new double[9];
	
	private boolean render1 = false, render2 = false;
    
    private double[] dummyangle = new double[3];
    
	public double getRotation() {return rotation;}
    
	public boolean render1() {return render1;}

	public double[] from1() {return from1;}

	public double[] to1() {return to1;}

	public double[] fixedfrom1() {return fixedfrom1;}

	public double[] fixedto1() {return fixedto1;}
	
	public double[] angle1() {return angle1;}

	public boolean render2() {return render2;}

	public double[] from2() {return from2;}

	public double[] to2() {return to2;}

	public double[] fixedfrom2() {return fixedfrom2;}

	public double[] fixedto2() {return fixedto2;}
	
	public double[] angle2() {return angle2;}
    
	public double[] getInsulatorPositionArray(){return insulatorPositionArray;}
	
	public double getInsulatorLength(){return insulatorLength;}
	
	/////////////////////////////////////////////////////
    public TransmissionTowerRenderHelper(TileEntity te, double insulatorLength, double[] insulatorPositionArray){
    	this.te = te;
    	this.insulatorLength = insulatorLength;
    	this.insulatorPositionArray = insulatorPositionArray;
    }
    
    private static void getRotatedXZ(double[] param, double rotation, int x, int z){	//ret: x,z, real MC coord
    	double lrx = param[0];
    	double lrz = param[1];
		param[0] = lrz * Math.sin(rotation/180*Math.PI) + lrx * Math.cos(rotation/180*Math.PI) + 0.5F + x;
		param[1] = lrz * Math.cos(rotation/180*Math.PI) - lrx * Math.sin(rotation/180*Math.PI) + 0.5F + z;
    }
    
    private static void transformCoord(double[] param, double rotation, int x, int z){	//ret: x,z, real MC coord
    	for (int i=0; i<9; i+=3){
    		double lrx = param[i];
    		double lrz = param[i+2];
    		param[i] = lrz * Math.sin(rotation/180*Math.PI) + lrx * Math.cos(rotation/180*Math.PI) + 0.5F + x;
    		param[i+2] = lrz * Math.cos(rotation/180*Math.PI) - lrx * Math.sin(rotation/180*Math.PI) + 0.5F + z;
    	}

    }
    
    private void findVirtualConnection(TileEntity tileEntity, int neighborX, int neighborY, int neighborZ, double[] from, double[] to){   
    	ISETransmissionTower curTw = (ISETransmissionTower) tileEntity;
    	double curRotation = curTw.getRotation()*45 - 90;
		
		double[] curInsulatorXZ1 = new double[] {insulatorPositionArray[3], insulatorPositionArray[5]};
		getRotatedXZ(curInsulatorXZ1, curRotation, tileEntity.xCoord, tileEntity.zCoord);
		
		if (insulatorPositionArray.length > 9){
			double[] curInsulatorXZ2 = new double[] {insulatorPositionArray[12], insulatorPositionArray[14]};
			getRotatedXZ(curInsulatorXZ2, curRotation, tileEntity.xCoord, tileEntity.zCoord);
			double dc1n = SEMathHelper.distanceOf(curInsulatorXZ1[0], curInsulatorXZ1[1], neighborX, neighborZ);
			double dc2n = SEMathHelper.distanceOf(curInsulatorXZ2[0], curInsulatorXZ2[1], neighborX, neighborZ);
			
			if (dc2n<dc1n){
				for (int i=0; i<9; i++){
					from[i] = insulatorPositionArray[i+9];
					to[i] = insulatorPositionArray[i+9];
				}
			}else{
				for (int i=0; i<9; i++){
					from[i] = insulatorPositionArray[i];
					to[i] = insulatorPositionArray[i];
				}
			}
		}else{
			for (int i=0; i<9; i++){
				from[i] = insulatorPositionArray[i];
				to[i] = insulatorPositionArray[i];
			}
		}
		
		//Transform to 'Real' MC coordinates
		transformCoord(from, curRotation, tileEntity.xCoord, tileEntity.zCoord);
		transformCoord(to, curRotation, neighborX, neighborZ);
		from[1] += tileEntity.yCoord;
		from[4] += tileEntity.yCoord;
		from[7] += tileEntity.yCoord;
		to[1] += neighborY;
		to[4] += neighborY;
		to[7] += neighborY;
		
		swapIfIntersect(from, to);
    }
    
    private void findConnection(TileEntity neighbor, double[] from, double[] to){    	
    	ISETransmissionTower neighborTw = (ISETransmissionTower) neighbor;
    	
		
		double neighborRotation = neighborTw.getRotation()*45 - 90;
		double[] neighborInsulatorPositionArray = neighborTw.getRenderHelper().getInsulatorPositionArray();		
		
		
		double[] curInsulatorXZ1 = new double[] {insulatorPositionArray[3], insulatorPositionArray[5]};
		getRotatedXZ(curInsulatorXZ1, rotation, te.xCoord, te.zCoord);
		
		double[] neighborInsulatorXZ1 = new double[] {neighborInsulatorPositionArray[3], neighborInsulatorPositionArray[5]};
		getRotatedXZ(neighborInsulatorXZ1, neighborRotation, neighbor.xCoord, neighbor.zCoord);
		
		double[] curInsulatorXZ2 = null;
		if (insulatorPositionArray.length > 9){
			curInsulatorXZ2 = new double[] {insulatorPositionArray[12], insulatorPositionArray[14]};
			getRotatedXZ(curInsulatorXZ2, rotation, te.xCoord, te.zCoord);
		}
		
		double[] neighborInsulatorXZ2 = null;
		if (neighborInsulatorPositionArray.length > 9){
			neighborInsulatorXZ2 = new double[] {neighborInsulatorPositionArray[12], neighborInsulatorPositionArray[14]};
			getRotatedXZ(neighborInsulatorXZ2, neighborRotation, neighbor.xCoord, neighbor.zCoord);
		}
		
		
		
		
		
		if (curInsulatorXZ2 == null && neighborInsulatorXZ2 == null){
			for (int i=0; i<9; i++){
				from[i] = insulatorPositionArray[i];
				to[i] = neighborInsulatorPositionArray[i];
			}
		}
		else if (curInsulatorXZ2 != null && neighborInsulatorXZ2 == null){
			double dc1n1 = SEMathHelper.distanceOf(curInsulatorXZ1, neighborInsulatorXZ1);
			double dc2n1 = SEMathHelper.distanceOf(curInsulatorXZ2, neighborInsulatorXZ1);
			
			if (dc2n1<dc1n1){
				for (int i=0; i<9; i++){
					from[i] = insulatorPositionArray[i+9];
					to[i] = neighborInsulatorPositionArray[i];
				}
			}else{
				for (int i=0; i<9; i++){
					from[i] = insulatorPositionArray[i];
					to[i] = neighborInsulatorPositionArray[i];
				}
			}
		}
		else if (curInsulatorXZ2 == null && neighborInsulatorXZ2 != null){
			double dc1n1 = SEMathHelper.distanceOf(curInsulatorXZ1, neighborInsulatorXZ1);
			double dc1n2 = SEMathHelper.distanceOf(curInsulatorXZ1, neighborInsulatorXZ2);
			
			if (dc1n2<dc1n1){
				for (int i=0; i<9; i++){
					from[i] = insulatorPositionArray[i];
					to[i] = neighborInsulatorPositionArray[i+9];
				}
			}else{
				for (int i=0; i<9; i++){
					from[i] = insulatorPositionArray[i];
					to[i] = neighborInsulatorPositionArray[i];
				}
			}
		}
		else if (curInsulatorXZ2 != null && neighborInsulatorXZ2 != null){
			double dc1n1 = SEMathHelper.distanceOf(curInsulatorXZ1, neighborInsulatorXZ1);
			double dc1n2 = SEMathHelper.distanceOf(curInsulatorXZ1, neighborInsulatorXZ2);
			double dc2n1 = SEMathHelper.distanceOf(curInsulatorXZ2, neighborInsulatorXZ1);
			double dc2n2 = SEMathHelper.distanceOf(curInsulatorXZ2, neighborInsulatorXZ2);
					
			for (int i=0; i<9; i++){
				from[i] = insulatorPositionArray[i];
				to[i] = neighborInsulatorPositionArray[i];
			}
			if (dc1n2<dc1n1){
				dc1n1 = dc1n2;
				for (int i=0; i<9; i++){
					from[i] = insulatorPositionArray[i];
					to[i] = neighborInsulatorPositionArray[i+9];
				}
			}
			if (dc2n1<dc1n1){
				dc1n1 = dc2n1;
				for (int i=0; i<9; i++){
					from[i] = insulatorPositionArray[i+9];
					to[i] = neighborInsulatorPositionArray[i];
				}
			}
			if (dc2n2<dc1n1){
				for (int i=0; i<9; i++){
					from[i] = insulatorPositionArray[i+9];
					to[i] = neighborInsulatorPositionArray[i+9];
				}
			}
		}
		
		//Transform to 'Real' MC coordinates
		transformCoord(from, rotation, te.xCoord, te.zCoord);
		transformCoord(to, neighborRotation, neighbor.xCoord, neighbor.zCoord);
		from[1] += te.yCoord;
		from[4] += te.yCoord;
		from[7] += te.yCoord;
		to[1] += neighbor.yCoord;
		to[4] += neighbor.yCoord;
		to[7] += neighbor.yCoord;
		
		swapIfIntersect(from, to);
    }
    
    public static void swapIfIntersect(double[] from, double[] to){
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
    
    private static double calcInitSlope(double xStart, double yStart, double zStart, double xEnd, double yEnd, double zEnd, double tension){
    	double length = SEMathHelper.distanceOf(xStart, yStart, zStart, xEnd, yEnd, zEnd);
    	double b = 4 * tension / length;
    	double a = -b / length;
    	return -Math.atan(2*a+b);
    }
    
    //I: from I: to O: fixed, angle
    private static void fixConnectionPoints(double[] from, double[] to, double[] angles, double[] fixedfrom, double insulatorLength, double tension){
    	double distance = SEMathHelper.distanceOf(to,from);
    	
        angles[0] = calcInitSlope(from[0], from[1], from[2], to[0], to[1], to[2], tension) + Math.atan((to[1]-from[1])/distance);
        double lcos = insulatorLength * Math.cos(angles[0]);
        double atan = Math.atan2(to[0] - from[0], from[2] - to[2]);        
        fixedfrom[0] = from[0] + lcos * Math.sin(atan);
        fixedfrom[1] = from[1] + insulatorLength * Math.sin(angles[0]);
        fixedfrom[2] = from[2] - lcos * Math.cos(atan);
        
        angles[1] = calcInitSlope(from[3], from[4], from[5], to[3], to[4], to[5], tension) + Math.atan((to[4]-from[4])/distance);
        lcos = insulatorLength * Math.cos(angles[1]);
        atan = Math.atan2(to[3] - from[3], from[5] - to[5]);
        fixedfrom[3] = from[3] + lcos * Math.sin(atan);
        fixedfrom[4] = from[4] + insulatorLength * Math.sin(angles[1]);
        fixedfrom[5] = from[5] - lcos * Math.cos(atan);
        
        angles[2] = calcInitSlope(from[6], from[7], from[8], to[6], to[7], to[8], tension) + Math.atan((to[7]-from[7])/distance);
        lcos = insulatorLength * Math.cos(angles[2]);
        atan = Math.atan2(to[6] - from[6], from[8] - to[8]);
        fixedfrom[6] = from[6] + lcos * Math.sin(atan);
        fixedfrom[7] = from[7] + insulatorLength * Math.sin(angles[2]);
        fixedfrom[8] = from[8] - lcos * Math.cos(atan);
    }
    
    public void updateRenderData(int x1, int y1, int z1, int x2, int y2, int z2){		
		rotation = ((ISETransmissionTower)te).getRotation()*45 - 90;
        
		TileEntity neighbor1 = te.getWorldObj().getTileEntity(x1, y1, z1);
		TileEntity neighbor2 = te.getWorldObj().getTileEntity(x2, y2, z2);
		
		if (y1 == -1){
			render1 = false;
		}else{
			render1 = true;
			if (neighbor1 == null){
				findVirtualConnection(te, x1, y1, z1, from1, to1);
				for (int i=0; i<9; i++)
					fixedto1[i] = to1[i];
			}
			else{
				findConnection(neighbor1, from1, to1);
				
				if (((ISETransmissionTower) neighbor1).getRenderHelper().getInsulatorPositionArray().length > 9)
					fixConnectionPoints(to1, from1, dummyangle, fixedto1, ((ISETransmissionTower) neighbor1).getRenderHelper().getInsulatorLength(), 3);
				else{
					for (int i=0; i<9; i++)
						fixedto1[i] = to1[i];
				}
			}
				
			if (insulatorPositionArray.length > 9){
				fixConnectionPoints(from1, to1, angle1, fixedfrom1, insulatorLength, 3);
			}else{
				for (int i=0; i<9; i++)
					fixedfrom1[i] = from1[i];
			}
		}
			
		if (y2 == -1){
			render2 = false;
		}else{
			render2 = true;
			if (neighbor2 == null){
				findVirtualConnection(te, x2, y2, z2, from2, to2);
				for (int i=0; i<9; i++)
					fixedto2[i] = to2[i];	
			}
			else{
				findConnection(neighbor2, from2, to2);
				
				if (((ISETransmissionTower) neighbor2).getRenderHelper().getInsulatorPositionArray().length > 9)
					fixConnectionPoints(to2, from2, dummyangle, fixedto2, ((ISETransmissionTower) neighbor2).getRenderHelper().getInsulatorLength(), 3);
				else{
					for (int i=0; i<9; i++)
						fixedto2[i] = to2[i];				
				}
			}
				
			
			if (insulatorPositionArray.length > 9){
				fixConnectionPoints(from2, to2, angle2, fixedfrom2, insulatorLength, 3);
			}else{
				for (int i=0; i<9; i++)
					fixedfrom2[i] = from2[i];
			}
			
		}
    }
}
