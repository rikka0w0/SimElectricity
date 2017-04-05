package simElectricity.Templates.Client.Render;

public interface ITransmissionTower {
	
	int[] getNeighborCoordArray();
	double getInsulatorLength();
	double getWireTension();
	double[] getInsulatorPositionArray();//{type,x1,y1,z1,x2,y2,z2,x3,y3,z3}
	int getRotation();
}
