package simElectricity.API.EnergyTile;

public interface ISEDiodeInput extends ISESubComponent{
	ISEDiodeOutput getOutput();
	double getForwardResistance();
}
