package simElectricity.API.Tile;

import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.ISEPlaceable;
import simElectricity.API.DataProvider.ISEComponentDataProvider;
import simElectricity.API.EnergyTile.ISESimulatable;

public interface ISECableTile extends ISEComponentDataProvider, ISEPlaceable{
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
    
    public ISESimulatable getNode();
    
    boolean canConnectOnSide(ForgeDirection direction);
    
    boolean isGridLinkEnabled();
}
