package simelectricity.api.components;

public interface ISECableBase extends ISEComponentParameter {
    /**
     * @return the resistance between this cable node and any other neighbor nodes
     * <p/>
     * Check out SimElectricity wikipedia for circuit models and more details
     */
    double getResistance();

    boolean hasShuntResistance();

    /**
     * Shunt resistance is the resistance placed between the cable node and the ground (0V reference)
     * Sometimes used by cover panels
     * @return must be a non-zero value, if shunt resistance is not applicable, hasShuntResistance() should return false.
     */
    double getShuntResistance();
}
