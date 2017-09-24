package simelectricity.api.node;

/**
 * An abstract interface representing a circuit block,
 * such as transformer primary winding, transformer secondary winding e.t.c.
 * <p/>
 * Use {@link simelectricity.api.SEAPI.energyNetAgent.newComponent} to create SubComponents.
 * @T internal use only
 */

public interface ISESubComponent<T extends ISESubComponent> extends ISESimulatable {
	T getComplement();
}
