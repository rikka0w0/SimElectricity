package simElectricity.API.EnergyTile;

public interface ISEVoltageSource extends ISESubComponent{
	public double getOutputVoltage();
	
	public double getResistance();
}
