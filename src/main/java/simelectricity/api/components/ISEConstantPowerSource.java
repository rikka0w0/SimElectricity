package simelectricity.api.components;

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
