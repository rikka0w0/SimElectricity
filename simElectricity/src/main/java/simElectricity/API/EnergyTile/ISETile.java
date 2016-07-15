package simElectricity.API.EnergyTile;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * A complex object that is able to have at most 6 nodes in simulation
 * <p/>
 * Can be used to make transformers, regulators, diodes, switches which have more than 1 terminal
 */
public interface ISETile extends ISEPlaceable{
	public int getNumberOfComponents();
	
	/**
	 * Returns a array containing directions which is allowed to be connected
	 */
	public ForgeDirection[] getValidDirections();
	
	public ISESubComponent getComponent(ForgeDirection side);
}
