package simelectricity.api.client;

public interface ITransmissionTowerRenderHelper {
	double getRotation();
	
	boolean render1();
	double[] from1();
	double[] to1();
	double[] fixedfrom1();
	double[] fixedto1();
	double[] angle1();
	
	boolean render2();
	double[] from2();
	double[] to2();
	double[] fixedfrom2();
	double[] fixedto2();
	double[] angle2();
	
	void updateRenderData(int x1, int y1, int z1, int x2, int y2, int z2);
}
