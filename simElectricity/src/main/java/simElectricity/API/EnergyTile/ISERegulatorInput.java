package simElectricity.API.EnergyTile;

public interface ISERegulatorInput extends ISESubComponent{
	public ISERegulatorOutput getOutput();
	
	public ISERegulatorController getController();
	
	public double getRegulatedVoltage();
	
	public double getOutputResistance();
}
