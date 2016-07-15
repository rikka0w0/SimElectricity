package simElectricity.API.EnergyTile;

/**
 * Represents a constant power load
 * <p/>
 * Within a certain range, the power consumed by the load is constant
 * <p/>
 * Otherwise it behaves just like a resistor
 * <p/>
 * See SimElectricity wikipedia for circuit models and more informations
 */
public interface ISEConstantPowerLoad extends ISESubComponent{
	/**
	 * Returns the desired power that is consumed by this component
	 */
	double getRatedPower();
	
	/**
	 * Returns the minimum resistance allowed
	 * <p/>
	 * Usually equals the square of the minimum allow working voltage on the rated power
	 */
	double getMinimumResistance();
	
	/**
	 * Returns the maximum resistance allowed
	 * <p/>
	 * Usually equals the square of the maximum allow working voltage on the rated power
	 */
	double getMaximumResistance();
	
	/**
	 * If return false, the load will be ignored during simulation
	 */	
	boolean isEnabled();
}
