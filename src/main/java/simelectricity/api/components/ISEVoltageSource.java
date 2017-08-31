package simelectricity.api.components;


/**
 * A voltage source in series with a resistance, a simple model for machines or generators.
 */
public interface ISEVoltageSource extends ISEComponentParameter {
    /**
     * @return the voltage of the internal voltage source
     * <p/>
     * 0 for loads, positive values for generators
     */
    double getOutputVoltage();

    /**
     * @return the resistance in series with the voltage source
     * </p>
     * For generators, it is the output impedance. For loads, it is the load impedance.
     */
    double getResistance();
}
