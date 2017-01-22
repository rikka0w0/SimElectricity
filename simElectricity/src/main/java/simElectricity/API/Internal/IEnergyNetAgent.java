package simElectricity.API.Internal;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import simElectricity.API.DataProvider.ISEComponentDataProvider;
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.EnergyTile.ISESubComponent;

public interface IEnergyNetAgent {
    /**
     * Return the voltage of a ISESimulatable instance, RELATIVE TO GROUND!
     * 
     * @param node The ISESimulatable instance
     * @param world The world that the ISESimulatable instance is in
     * @return the voltage of the node, in volts
     */
	public double getVoltage(ISESimulatable node);
	
	public ISESubComponent newComponent(TileEntity dataProviderTileEntity);
	
	public ISESubComponent newComponent(ISEComponentDataProvider dataProvider, TileEntity parent);
	
	public ISESimulatable newCable(TileEntity dataProviderTileEntity);
}
