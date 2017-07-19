package simelectricity.api;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Implement this interface in a tileEntity to allow the wrench to change its functional side
 */
public interface ISEWrenchable {
    boolean canWrenchBeUsed(ForgeDirection side);
    
    /**
     * Called when the functional side is going to be set by the wrench, DONT use this when placing the block!
     * </p>
     * Note: SERVER ONLY! Initiate a server->client sync if needed
     */
    void onWrenchAction(ForgeDirection side, boolean isCreativePlayer);
}
