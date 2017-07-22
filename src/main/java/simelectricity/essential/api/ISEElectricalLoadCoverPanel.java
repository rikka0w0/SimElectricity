package simelectricity.essential.api;

/**
 *	Implement this interface indicates that the cover panel is a electrical load (it consumes power)
 */
public interface ISEElectricalLoadCoverPanel extends ISEElectricalCoverPanel{
	double getResistance();
}
