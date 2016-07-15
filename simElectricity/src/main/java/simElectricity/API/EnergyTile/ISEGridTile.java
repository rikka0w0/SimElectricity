package simElectricity.API.EnergyTile;

import java.util.List;

import net.minecraft.tileentity.TileEntity;

/**
 * This interface should be implemented by the tileEntity, which is responsible for HV tower/transformer rendering
 *
 */
public interface ISEGridTile {
    /**
     * This function will be called by the grid manager when a ISEGridObject is going to associate with the ISEGridTile
     * <p/>
     * Make sure you store the instance of ISEGridObject in this function
     */
	void setGridObject(ISEGridObject gridObj);
	
    /**
     * This function will be called every time when the neighbor list of its ISEGridObject has changed
     * <p/>
     * E.g. Connection established, map loading, Connection removed...
     */
	void onGridNeighborUpdated();
	
    /**
     * Called by HV cable items, to decide whether this ISEGridObject can be connected
     */
	boolean canConnect();
}