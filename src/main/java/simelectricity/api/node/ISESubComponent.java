package simelectricity.api.node;

import simelectricity.api.components.ISEComponentParameter;

/**
 * An abstract interface representing a circuit block,
 * such as transformer primary winding, transformer secondary winding e.t.c.
 * <p/>
 * Use {@link simelectricity.api.SEAPI.energyNetAgent.newComponent} to create SubComponents.
 * API users should not implement this interface anywhere!
 */

public interface ISESubComponent<T extends ISESubComponent<?>> extends ISESimulatable, ISEComponentParameter {

}
