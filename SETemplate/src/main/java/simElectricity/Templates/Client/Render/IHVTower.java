package simElectricity.Templates.Client.Render;

import net.minecraft.tileentity.TileEntity;

public interface IHVTower {
	float getWireTension();
	
	float[] offsetArray();

	int[] getNeighborInfo();

	int getFacing();
}
