package simelectricity.api.node;

/**
 * ISESimulatable represents a circuit node
 * API users use these method to retrieve
 * API users should not implement this interface anywhere!
 */
public interface ISESimulatable {
    /**
     * @return the node voltage, in volts, refer to ground
     */
    double getVoltage();

    /**
     * @return the magnitude of current flow through device/cable, refer to ground, NaN if not applicable
     */
    double getCurrentMagnitude();

    /**
     * @return true if the given ISESimulatable has DIRECT RESISTIVE connection with the current ISESimulatable
     */
    boolean hasResistiveConnection(ISESimulatable neighbor);
}
