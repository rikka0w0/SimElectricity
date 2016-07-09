package simElectricity.API.EnergyTile;

import java.util.List;

public interface ISEJunction extends ISESubComponent{
    /**
     * Return the neighbors of this junction
     * Do not have to be geographically next to this tileEntity!
     */
	void getNeighbors(List<ISESimulatable> list);


    /**
     * A advanced version of {@link simElectricity.API.EnergyTile.IBaseComponent#getResistance() getResistance()} in {@link simElectricity.API.EnergyTile.IBaseComponent}
     * <p/>
     * Return 0 in {@link simElectricity.API.EnergyTile.IBaseComponent#getResistance() getResistance()} in {@link simElectricity.API.EnergyTile.IBaseComponent} to make this function valid
     * <p/>
     * Should return the resistance between this tileEntity and the neighbor
     */
    double getResistance(ISESimulatable neighbor);
}
