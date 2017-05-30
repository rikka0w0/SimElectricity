package simElectricity.API.DataProvider;

public interface ISESwitchData extends ISEComponentDataProvider{
	boolean isOn();
	double getResistance();
}
