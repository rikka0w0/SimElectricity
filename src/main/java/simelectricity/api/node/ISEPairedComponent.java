package simelectricity.api.node;

import javax.annotation.Nonnull;

public interface ISEPairedComponent<T extends ISEPairedComponent<?>> extends ISESubComponent<T> {
	@Nonnull
	T getComplement();
}
