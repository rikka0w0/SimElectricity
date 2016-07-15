package simElectricity.API.EnergyTile;

import java.util.LinkedList;

/**
 * A ISEGridObject is the object stored in the world's grid, it is independent of the tileEntity
 * <p/>
 * However, if the chunk 'contains' the ISEGridObject is loaded, the ISEGridObject will be associated with the tileEntity
 * <p/>
 * The tileEntity must implements ISEGridTile interface
 */
public interface ISEGridObject extends ISESimulatable{
	int getXCoord();
	int getYCoord();
	int getZCoord();
	
    /**
     * Returns a list of neighbors
     */
	LinkedList<ISEGridObject> getNeighborList();
	
    /**
     * Returns the resistance between this node and the neighbor node
     */
	double getResistance(ISEGridObject neighbor);
}
