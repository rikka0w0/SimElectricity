package simelectricity.api.node;

import net.minecraft.util.math.BlockPos;

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
	
	BlockPos getPos();
	int getType();
	
    /**
     * Returns a list of neighbors
     */
	ISEGridNode[] getNeighborList();
}
