package simelectricity.api.tile;

import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.api.ISEPlaceable;
import simelectricity.api.components.ISEComponentDataProvider;
import simelectricity.api.node.ISESimulatable;

public interface ISECableTile extends ISEComponentDataProvider, ISEPlaceable{
    /**
     * Returns the color of the cable
     * <p/>
     * Color 0 is considered as universal, that means cables with any color can connect to a cable with universal color 0
     */
	
	/**
	 * @return an integer represents the color of the cable
	 * <p/>
	 * A cable tile with color 0 can connect to any other cables,
	 * a cable tile with non-zero color can only connect to cables which has the same color or 0 color.
	 */
    int getColor();
    
    /**
     * @return the resistance between the cable node and any other neighbor nodes
     * <p/>
     * Check SimElectricity wikipedia for circuit models and more informations
     */
    public double getResistance();
    
    public ISESimulatable getNode();
    
    boolean canConnectOnSide(ForgeDirection direction);
    
    boolean isGridLinkEnabled();
}
