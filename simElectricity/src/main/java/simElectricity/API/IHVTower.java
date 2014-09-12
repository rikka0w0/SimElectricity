package simElectricity.API;

import net.minecraft.tileentity.TileEntity;

public interface IHVTower {
	float getWireTension();
	
	float[] offsetArray();

	int[] getNeighborInfo();

	int getFacing();
	
	void addNeighbor(TileEntity te);
	
	boolean hasVacant();
}
