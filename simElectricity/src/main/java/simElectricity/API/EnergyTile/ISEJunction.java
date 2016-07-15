package simElectricity.API.EnergyTile;

import java.util.List;

/**
 * Represents a circuit node, more advanced, more flexible
 */
public interface ISEJunction extends ISESubComponent{
    /**
     * Add all neighbors of this junction to the list in this function
     * </p>
     * Do not have to be geographically next to this tileEntity!
     * </p>
     * Use list.add()
     */
	void getNeighbors(List<ISESimulatable> list);


    /**
     * Should return the resistance between this node and the neighbor node
     */
    double getResistance(ISESimulatable neighbor);
}
