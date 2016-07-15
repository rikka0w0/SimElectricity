package simElectricity.API.EnergyTile;

import java.util.List;

import net.minecraft.tileentity.TileEntity;

public interface ISEGridTile {
	void setGridObject(ISEGridObject gridObj);
	void onGridNeighborUpdated();
	boolean canConnect();
}