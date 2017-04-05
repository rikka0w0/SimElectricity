package simElectricity.API.Client;

public interface ITransmissionTower {
	ITransmissionTowerRenderHelper getRenderHelper();
	double getInsulatorLength();
	double[] getInsulatorPositionArray();
	int getRotation();
}
