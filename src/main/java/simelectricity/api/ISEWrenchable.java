package simelectricity.api;

import net.minecraft.core.Direction;

/**
 * For TileEntities only, implementing this makes it a target for wrenches,
 * wrenches are used to change machines' functional side (The side used to interact with the EnergyNet)
 */
public interface ISEWrenchable {
    /**
     * @return false to reject the action
     */
    boolean canWrenchBeUsed(Direction side);

    /**
     * Called when the functional side is about be set by the wrench
     * </p>
     * Note: Called from SERVER ONLY! Initiate a server->client sync if needed
     */
    void onWrenchAction(Direction side, boolean isCreativePlayer);
}
