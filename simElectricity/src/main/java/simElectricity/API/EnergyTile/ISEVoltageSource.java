package simElectricity.API.EnergyTile;

/**
 * A simple electrical model for machines or generators
 */
public interface ISEVoltageSource extends ISESubComponent{
	/**
	 * Returns the voltage of the internal voltage source
	 * <p/>
	 * 0 for loads, positive values for generators
	 */
	public double getOutputVoltage();
	
	/**
	 * Returns the internal resistance
	 */
	public double getResistance();
}
