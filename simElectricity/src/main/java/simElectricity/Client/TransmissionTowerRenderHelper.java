package simElectricity.Client;

import net.minecraft.tileentity.TileEntity;
import simElectricity.API.SEAPI;
import simElectricity.API.Client.ITransmissionTower;
import simElectricity.API.Client.ITransmissionTowerRenderHelper;

public class TransmissionTowerRenderHelper implements ITransmissionTowerRenderHelper{
	private TileEntity te;
	
	private double rotation;
	
	private double[] from1 = new double[9], to1 = new double[9];
	private double[] fixedfrom1 = new double[9], fixedto1 = new double[9];
	private double[] angle1 = new double[3];
	
	private double[] from2 = new double[9], to2 = new double[9];
	private double[] angle2 = new double[3];
	private double[] fixedfrom2 = new double[9],fixedto2 = new double[9];
	
	private boolean render1 = false, render2 = false;
    
    private double[] dummyangle = new double[3];
    
    @Override
	public double getRotation() {return rotation;}
    
	@Override
	public boolean render1() {return render1;}

	@Override
	public double[] from1() {return from1;}

	@Override
	public double[] to1() {return to1;}

	@Override
	public double[] fixedfrom1() {return fixedfrom1;}

	@Override
	public double[] fixedto1() {return fixedto1;}
	
	@Override
	public double[] angle1() {return angle1;}

	@Override
	public boolean render2() {return render2;}

	@Override
	public double[] from2() {return from2;}

	@Override
	public double[] to2() {return to2;}

	@Override
	public double[] fixedfrom2() {return fixedfrom2;}

	@Override
	public double[] fixedto2() {return fixedto2;}
	
	@Override
	public double[] angle2() {return angle2;}
    
	/////////////////////////////////////////////////////
    public TransmissionTowerRenderHelper(TileEntity te){
    	this.te = te;
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
    	double ret = 0;
    	for (int i=0; i<Start.length; i++)
    		ret += (Start[i] - End[i])*(Start[i] - End[i]);

    	return Math.sqrt(ret);
    }
    
    private double distanceOf(double[] Start, double x, double z) {
        return Math.sqrt((Start[0] - x)*(Start[0] - x) + (Start[1] - z)*(Start[1] - z));
    }
    
    private void findVirtualConnection(TileEntity tileEntity, int neighborX, int neighborY, int neighborZ, double[] from, double[] to){   
    	ITransmissionTower curTw = (ITransmissionTower) tileEntity;
    	double curRotation = curTw.getRotation()*45 - 90;
		double[] curInsulatorPositionArray = curTw.getInsulatorPositionArray();	
		
		double[] curInsulatorXZ1 = new double[] {curInsulatorPositionArray[3], curInsulatorPositionArray[5]};
		getRotatedXZ(curInsulatorXZ1, curRotation, tileEntity.xCoord, tileEntity.zCoord);
		
		if (curInsulatorPositionArray.length > 9){
			double[] curInsulatorXZ2 = new double[] {curInsulatorPositionArray[12], curInsulatorPositionArray[14]};
			getRotatedXZ(curInsulatorXZ2, curRotation, tileEntity.xCoord, tileEntity.zCoord);
			double dc1n = distanceOf(curInsulatorXZ1, neighborX, neighborZ);
			double dc2n = distanceOf(curInsulatorXZ2, neighborX, neighborZ);
			
			if (dc2n<dc1n){
				for (int i=0; i<9; i++){
					from[i] = curInsulatorPositionArray[i+9];
					to[i] = curInsulatorPositionArray[i+9];
				}
			}else{
				for (int i=0; i<9; i++){
					from[i] = curInsulatorPositionArray[i];
					to[i] = curInsulatorPositionArray[i];
				}
			}
		}else{
			for (int i=0; i<9; i++){
				from[i] = curInsulatorPositionArray[i];
				to[i] = curInsulatorPositionArray[i];
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
    
    private void findConnection(TileEntity tileEntity, TileEntity neighbor, double[] from, double[] to){    	
    	ITransmissionTower curTw = (ITransmissionTower) tileEntity;
    	ITransmissionTower neighborTw = (ITransmissionTower) neighbor;
    	
    	double curRotation = curTw.getRotation()*45 - 90;
		double[] curInsulatorPositionArray = curTw.getInsulatorPositionArray();		
		
		double neighborRotation = neighborTw.getRotation()*45 - 90;
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
		
		swapIfIntersect(from, to);
    }
    
    private void swapIfIntersect(double[] from, double[] to){
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
    	double distance = distanceOf(to,from);
    	
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
    
    @Override
    public void updateRenderData(int[] neighborCoords){		
    	ITransmissionTower tw = (ITransmissionTower)te;
    	
		rotation = tw.getRotation()*45 - 90;
        
		TileEntity neighbor1 = te.getWorldObj().getTileEntity(neighborCoords[0], neighborCoords[1], neighborCoords[2]);
		TileEntity neighbor2 = te.getWorldObj().getTileEntity(neighborCoords[3], neighborCoords[4], neighborCoords[5]);
		
		if (neighborCoords[1] == -1){
			render1 = false;
		}else{
			render1 = true;
			if (neighbor1 == null){
				findVirtualConnection(te, neighborCoords[0], neighborCoords[1], neighborCoords[2], from1, to1);
				for (int i=0; i<9; i++)
					fixedto1[i] = to1[i];
			}
			else{
				findConnection(te, neighbor1, from1, to1);
				
				if (((ITransmissionTower) neighbor1).getInsulatorPositionArray().length > 9)
					fixConnectionPoints(to1, from1, dummyangle, fixedto1, ((ITransmissionTower) neighbor1).getInsulatorLength(), 3);
				else{
					for (int i=0; i<9; i++)
						fixedto1[i] = to1[i];
				}
			}
				
			if (tw.getInsulatorPositionArray().length > 9){
				fixConnectionPoints(from1, to1, angle1, fixedfrom1, tw.getInsulatorLength(), 3);
			}else{
				for (int i=0; i<9; i++)
					fixedfrom1[i] = from1[i];
			}
		}
			
		if (neighborCoords[4] == -1){
			render2 = false;
		}else{
			render2 = true;
			if (neighbor2 == null){
				findVirtualConnection(te, neighborCoords[3], neighborCoords[4], neighborCoords[5], from2, to2);
				for (int i=0; i<9; i++)
					fixedto2[i] = to2[i];	
			}
			else{
				findConnection(te, neighbor2, from2, to2);
				
				if (((ITransmissionTower) neighbor2).getInsulatorPositionArray().length > 9)
					fixConnectionPoints(to2, from2, dummyangle, fixedto2, ((ITransmissionTower) neighbor2).getInsulatorLength(), 3);
				else{
					for (int i=0; i<9; i++)
						fixedto2[i] = to2[i];				
				}
			}
				
			
			if (tw.getInsulatorPositionArray().length > 9){
				fixConnectionPoints(from2, to2, angle2, fixedfrom2, tw.getInsulatorLength(), 3);
			}else{
				for (int i=0; i<9; i++)
					fixedfrom2[i] = from2[i];
			}
			
		}
    }
}
