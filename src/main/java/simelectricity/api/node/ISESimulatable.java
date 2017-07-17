package simelectricity.api.node;

import simelectricity.api.components.ISEComponentParameter;

/**
 * An abstract interface which represents a circuit node
 */
public interface ISESimulatable {
	public ISESubComponent getComplement();
	public ISEComponentParameter getCachedParameters();
}
