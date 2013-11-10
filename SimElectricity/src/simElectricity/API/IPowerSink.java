package simElectricity.API;

public interface IPowerSink extends IBaseComponent {
	int getMaxSafeVoltage();

	void onOverVoltage();
}
