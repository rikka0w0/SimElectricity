package simelectricity.api.tile;

import simelectricity.api.ISEPlaceable;
import simelectricity.api.components.ISECableParameter;
import simelectricity.api.node.ISESimulatable;

public interface ISECableTile extends ISECableParameter, ISEPlaceable{
    public ISESimulatable getNode();
}
