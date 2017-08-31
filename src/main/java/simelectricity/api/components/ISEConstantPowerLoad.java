package simelectricity.api.components;


/**
 * Represents a constant power load
 * <p/>
 * Within a certain range, the load consumes a constant amount of power
 * <p/>
 * Otherwise it behaves just like a resistor
 * <p/>
 * See SimElectricity wikipedia for circuit models and more details
 */
public interface ISEConstantPowerLoad extends ISEComponentParameter {
    /**
     * @return regulated power level
     */
    double getRatedPower();

    /**
     * @return the minimum allowed resistance
     * <p/>
     * Usually equals to the square of the minimum input voltage on the rated power
     */
    double getMinimumResistance();

    /**
     * @return the maximum allowed resistance
     * <p/>
     * Usually equals to the square of the maximum input voltage on the rated power
     */
    double getMaximumResistance();

    /**
     * @return true if the load is turned on
     */
    boolean isEnabled();
}
