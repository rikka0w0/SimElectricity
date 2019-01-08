package simelectricity.api;

import net.minecraft.util.EnumFacing;

/**
 * For TileEntities only, example: cable implement this to allow crowbar remove installed cover panels
 */
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
