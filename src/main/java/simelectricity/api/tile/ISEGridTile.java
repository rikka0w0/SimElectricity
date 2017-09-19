package simelectricity.api.tile;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import simelectricity.api.node.ISEGridNode;

/**
 * For TileEntities only.
 */
public interface ISEGridTile {
    ISEGridNode getGridNode();

    /**
     * This function will be called by the grid manager once a ISEGridObject is going to associate with the ISEGridTile
     * <p/>
     * Make sure you store the ISEGridNode instance in your TileEntity
     * <p/>
     * Do NOT call this function anywhere else!
     * @param gridNode the ISEGridNode at the TileEntity's location
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
     * 
     * @param toPos null - selected by player, nonnull - target position
     * @return false to reject
     */
    boolean canConnect(@Nullable BlockPos toPos);
}