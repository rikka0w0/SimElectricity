package simElectricity.API.EnergyTile;

import net.minecraftforge.common.util.ForgeDirection;

public interface ISETile extends ISEPlaceable{
	public int getNumberOfComponents();
	
	public ForgeDirection[] getValidDirections();
	
	public ISESubComponent getComponent(ForgeDirection side);
}
