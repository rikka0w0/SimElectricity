package simElectricity.API.EnergyTile;

/**
 * Represents a conductor block, should be implemented by cable blocks
 */
public interface ISEConductor extends ISEPlaceable,ISESimulatable{
    /**
     * Returns the color of the cable
     * <p/>
     * Color 0 is considered as universal, that means cables with any color can connect to a cable with universal color 0
     */
    int getColor();
    
    /**
     * Returns the resistance between neighbor nodes
     * <p/>
     * See SimElectricity wikipedia for circuit models and more informations
     */
    public double getResistance();
}
