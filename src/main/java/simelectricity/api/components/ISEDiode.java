package simelectricity.api.components;


public interface ISEDiode extends ISEComponentParameter {
    /**
     * Recommend value: 0.1
     * @return minimum forward resistance
     */
    double getForwardResistance();

    /**
     * Recommend value: 1e-6
     * @return saturation current Is
     */
    double getSaturationCurrent();

    /**
     * Recommend value: 26e-6
     * @return thermal voltage Vt
     */
    double getThermalVoltage();
}
