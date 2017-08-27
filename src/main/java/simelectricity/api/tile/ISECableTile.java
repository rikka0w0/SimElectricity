package simelectricity.api.tile;

import simelectricity.api.components.ISECable;
import simelectricity.api.node.ISESimulatable;

public interface ISECableTile extends ISECable {
    ISESimulatable getNode();
}
