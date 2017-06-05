package simelectricity.api.components;

public interface ISESwitchData extends ISEComponentDataProvider{
	boolean isOn();
	double getResistance();
}
