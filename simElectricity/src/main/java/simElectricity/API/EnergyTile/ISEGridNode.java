package simElectricity.API.EnergyTile;

import java.util.LinkedList;

/**
 * A ISEGridObject is the object stored in the world's grid, it is independent of the tileEntity
 * <p/>
 * However, if the chunk which 'contains' the ISEGridObject is loaded, the energy net will associate the ISEGridObject with the tileEntity
 * <p/>
 * The tileEntity must implements ISEGridTile interface
 */
public interface ISEGridNode extends ISESimulatable{
	int getXCoord();
	int getYCoord();
	int getZCoord();
	
    /**
     * Returns a list of neighbors
     */
	LinkedList<ISESimulatable> getNeighborList();
	
    /**
     * Returns the resistance between this node and the neighbor node
     */
	double getResistance(ISEGridNode neighbor);
}
