package simelectricity.api;

import net.minecraft.util.EnumFacing;

/**
 * Implement this interface in a tileEntity to allow the wrench to change its functional side
 */
public interface ISEWrenchable {
    boolean canWrenchBeUsed(EnumFacing side);
    
    /**
     * Called when the functional side is going to be set by the wrench, DONT use this when placing the block!
     * </p>
     * Note: SERVER ONLY! Initiate a server->client sync if needed
     */
    void onWrenchAction(EnumFacing side, boolean isCreativePlayer);
}
