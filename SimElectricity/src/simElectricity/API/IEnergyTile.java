package simElectricity.API;

public interface IEnergyTile extends IBaseComponent {
	float getMaxSafeVoltage();

	void onOverVoltage();

	float getInternalResistance();

	// 0 is sink, other is source
	float getOutputVoltage();

}
