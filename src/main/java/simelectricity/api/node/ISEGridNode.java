package simelectricity.api.node;

import net.minecraft.util.math.BlockPos;

/**
 * A ISEGridNode represents a circuit node in the world's grid system, it is independent of its host tileEntity and chunk
 * <p/>
 * Once the host chunk is loaded, the EnergyNet will assign this ISEGridNode to the {@link simelectricity.api.tile.ISEGridTile}
 * at the same location
 */
public interface ISEGridNode extends ISESimulatable {
    int ISEGridNode_Wire = 0;
    int ISEGridNode_TransformerPrimary = 1;
    int ISEGridNode_TransformerSecondary = 2;

    BlockPos getPos();

    int getType();

    /**
     * @return a list of neighbor ISEGridNodes
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
