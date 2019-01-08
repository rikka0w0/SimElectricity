package simelectricity.api.components;

import net.minecraft.util.EnumFacing;

/**
 * Provides necessary parameters for a cable node
 */
public interface ISECable extends ISEComponentParameter {
    /**
     *
     * @return an integer which represents the color of the cable
     * <p/>
     * 0 - can connect to any other cables,
     * Otherwise - can only connect to cables with the same color or color 0.
     */
    int getColor();

    /**
     * @return the resistance between this cable node and any other neighbor nodes
     * <p/>
     * Check out SimElectricity wikipedia for circuit models and more details
     */
    double getResistance();

    /**
     * @return false to block any connection from the given side
     */
    boolean canConnectOnSide(EnumFacing side);

    /**
     * @return true to allow the link to cable node to the grid node at the same location
     */
    boolean isGridLinkEnabled();

    boolean hasShuntResistance();

    /**
     * Shunt resistance is the resistance placed between the cable node and the ground (0V reference)
     * Sometimes used by cover panels
     * @return must be a non-zero value, if shunt resistance is not applicable, hasShuntResistance() should return false.
     */
    double getShuntResistance();
}
