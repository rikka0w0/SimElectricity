package simelectricity.api.tile;

import simelectricity.api.node.ISEGridNode;

/**
 * For TileEntities only.
 */
public interface ISEGridTile {
    ISEGridNode getGridNode();

    /**
     * This function will be called by the grid manager once a ISEGridObject is going to associate with the ISEGridTile
     * <p/>
     * Make sure you  storethe ISEGridNode instance in your TileEntity
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
}