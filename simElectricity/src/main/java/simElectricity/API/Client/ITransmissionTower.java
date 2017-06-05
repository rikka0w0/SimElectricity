package simelectricity.api.client;

public interface ITransmissionTower {
	ITransmissionTowerRenderHelper getRenderHelper();
	double getInsulatorLength();
	double[] getInsulatorPositionArray();
	int getRotation();
}
