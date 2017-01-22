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
	public static final int ISEGridNode_Wire = 0;
	public static final int ISEGridNode_TransformerPrimary = 1;
	public static final int ISEGridNode_TransformerSecondary = 2;
	
	int getXCoord();
	int getYCoord();
	int getZCoord();
	int getType();
	
    /**
     * Returns a list of neighbors
     */
	LinkedList<ISESimulatable> getNeighborList();
	
    /**
     * Returns the resistance between this node and the neighbor node
     */
	double getResistance(ISEGridNode neighbor);
}
