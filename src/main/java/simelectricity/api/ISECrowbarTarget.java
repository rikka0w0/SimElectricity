package simelectricity.api;

import net.minecraft.util.EnumFacing;

public interface ISECrowbarTarget {
    /**
     * @return false to reject the action
     */
    boolean canCrowbarBeUsed(EnumFacing side);

    /**
     * Called when a player right-clicked the block with a crowbar
     *
     * Note: Called from SERVER ONLY! Initiate a server->client sync if needed
     */
    void onCrowbarAction(EnumFacing side, boolean isCreativePlayer);
}
