package SimElectricity.API;

public interface IPowerSink extends IBaseComponent{
	int getMaxSafeVoltage();
	void onOverVoltage();
}
