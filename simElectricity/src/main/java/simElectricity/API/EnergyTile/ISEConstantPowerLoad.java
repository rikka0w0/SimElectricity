package simElectricity.API.EnergyTile;

public interface ISEConstantPowerLoad extends ISESubComponent{
	double getRatedPower();
	double getMinimumResistance();
	double getMaximumResistance();
	boolean isEnabled();
}
