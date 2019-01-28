package simelectricity.api.tile;

import simelectricity.api.components.ISECable;
import simelectricity.api.node.ISESimulatable;

/**
 * For TileEntities only, represents a cable
 */
public interface ISECableTile extends ISECable {
    /**
     * @return Implementer should return the ISESimulatable from {@link simelectricity.api.SEAPI.energyNetAgent}
     */
    ISESimulatable getNode();

    /**
     * Called when it is necessary to set the cable's color
     * Note: After setting the color, it might be necessary to start a server-client synchronization
     * @param newColor
     */
    void setColor(int newColor);
}
