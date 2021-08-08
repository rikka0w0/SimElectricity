package simelectricity.api.components;

import net.minecraft.core.Direction;

/**
 * Provides necessary parameters for a cable node
 */
public interface ISECable extends ISECableBase {
    /**
     *
     * @return an integer which represents the color of the cable
     * <p/>
     * 0 - can connect to any other cables,
     * Otherwise - can only connect to cables with the same color or color 0.
     */
    int getColor();

    /**
     * @return false to block any connection from the given side
     */
    boolean canConnectOnSide(Direction side);

    /**
     * @return true to allow the link to cable node to the grid node at the same location
     */
    boolean isGridLinkEnabled();
}
