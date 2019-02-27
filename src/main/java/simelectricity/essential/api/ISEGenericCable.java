package simelectricity.essential.api;

import net.minecraft.util.EnumFacing;
import simelectricity.api.tile.ISECableTile;

public interface ISEGenericCable extends ISECoverPanelHost, ISECableTile, ISEChunkWatchSensitiveTile {

    /**
     * Called by cable render (may be custom implementation) to
     * determine if the cable block has connection on the given side
     * @param side
     * @return ture if electrically connected
     */
    boolean connectedOnSide(EnumFacing side);
}
