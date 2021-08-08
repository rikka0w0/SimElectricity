package simelectricity.api.tile;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import simelectricity.api.node.ISEGridNode;

/**
 * For TileEntities only. Once the containing chunk is loaded,
 * A tileEntity implemented this interface will be associated with its corresponding ISEGridObject.
 */
public interface ISEGridTile {
    ISEGridNode getGridNode();

    /**
     * This function will be called by the grid manager once a ISEGridObject is going to associate with the ISEGridTile
     * <p/>
     * Make sure you store the ISEGridNode instance in your BlockEntity
     * <p/>
     * Do NOT call this function anywhere else!
     * @param gridNode the ISEGridNode at the BlockEntity's location
     */
    void setGridNode(ISEGridNode gridNode);

    /**
     * Grid modifications (related to the ISEGridNode at this location) can trigger this function.
     * Do/Schedule rendering updates here.
     * <p/>
     * E.g. Connection established, map loading, Connection removed...
     */
    void onGridNeighborUpdated();
    
    /**
     * Called by HV cables or others to determine if the node can accept more connections
     * @param toPos null - selected by player, non-null - target position
     * @return false to reject
     */
    boolean canConnect(@Nullable BlockPos toPos);
}