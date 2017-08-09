package simelectricity.api.components;

import net.minecraft.util.EnumFacing;

public interface ISECable extends ISEComponentParameter{
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
    double getResistance();
    
    boolean canConnectOnSide(EnumFacing direction);
    
    boolean isGridLinkEnabled();
    
    boolean hasShuntResistance();
    
    double getShuntResistance();
}
