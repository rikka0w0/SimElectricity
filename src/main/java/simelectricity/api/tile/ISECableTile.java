package simelectricity.api.tile;

import simelectricity.api.components.ISECable;
import simelectricity.api.node.ISESimulatable;

/**
 * For TileEntities only, represents a cable
 */
public interface ISECableTile extends ISECable {
    ISESimulatable getNode();
}
