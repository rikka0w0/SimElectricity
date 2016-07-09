package simElectricity.API.EnergyTile;

public interface ISERegulatorInput extends ISESubComponent{
	public ISERegulatorOutput getOutput();
	
	public double getMinimumInputVoltage();
	
	public double getRegulatedVoltage();
	
	public double getMaximumInputVoltage();
	
	public double getOutputRipple();
	
	public double getOutputResistance();
}
