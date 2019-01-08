package simelectricity.api.components;

/**
 * Represents a constant power source
 * <p/>
 * Within a certain voltage range, the source provides a constant amount of power
 * <p/>
 * Otherwise it behaves just like a ideal voltage source in series with a resistance
 * <p/>
 * See SimElectricity wikipedia for circuit models and more details
 */
public interface ISEConstantPowerSource extends ISEComponentParameter, ISESwitchable {
    /**
     * @return the rated power level, Po
     */
    double getRatedPower();

    /**
     * @return the minimum output voltage, Vmin. Vint will be 2xVmin. Rmax will be Vint*Vint/4*Po
     */
    double getMinimumOutputVoltage();

    /**
     * @return the maximum output voltage, MUST be less than 2xVmin. Rmax will be (Vint-Vmax)*Vmax/Po
     */
    double getMaximumOutputVoltage();
}
