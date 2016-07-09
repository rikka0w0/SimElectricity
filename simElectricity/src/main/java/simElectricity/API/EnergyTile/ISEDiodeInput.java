package simElectricity.API.EnergyTile;

public interface ISEDiodeInput extends ISESubComponent{
	ISEDiodeOutput getOutput();
	double getVoltageDrop();
	double getForwardResistance();
	double getReverseResistance();
}
