package simelectricity.essential.api.coverpanel;

/**
 *	Able to respond to EnergyNet update;
 */
public interface ISEElectricalCoverPanel extends ISECoverPanel{
	/**
	 * The cover panel host is responsible for dispatching onEnergyNetUpdate to cover panels
	 * @param voltage the node voltage
	 */
	void onEnergyNetUpdate(double voltage);
	
	/**
	 * Called when the cover panel is placed by a player using itemStack
	 * @param voltage value just before the cover panel is installed
	 */
	void onPlaced(double voltage);
}
