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
     * @return a list of neighbors
     */
	ISEGridNode[] getNeighborList();
	
	/**
	 * Used by HV cable to check if can connect or not</p>
	 * 0 - Allows any coming connection (Not recommended, may crash the essential transmission line rendering system)</p>
	 * 1 - SWER (Single Wire Earth Return, for rural areas)</p>
	 * 2 - HVDC (High Voltage DC Transmission Line, for long range power transmission)
	 * 2 - AC   (Single-Phase AC, for power distribution)
	 * 3 - HVAC (3-Phase Delta connected transmission Line, for long and middle range power transmission)
	 * 4 - AC   (3-Phase Wye connected, for power distribution)
	 * 4 - AC   (Electrical Rail Systems)
	 */
	byte numOfParallelConductor();
}
