package simelectricity.essential.api;

/**
 *	Able to respond to EnergyNet update;
 */
public interface ISEElectricalCoverPanel extends ISECoverPanel{
	void onEnergyNetUpdate(double voltage);
	
	/**
	 * Called when the cover panel is placed by a player using itemStack
	 * @param voltage value just before the cover panel is installed
	 */
	void onPlaced(double voltage);
}
