package simElectricity.API.Internal;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import simElectricity.API.DataProvider.ISEComponentDataProvider;
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.EnergyTile.ISESubComponent;

public interface IEnergyNetAgent {
    /**
     * Return the voltage of a ISESimulatable instance, RELATIVE TO GROUND!
     * 
     * @param node The ISESimulatable instance
     * @return the voltage of the node, in volts
     */
	double getVoltage(ISESimulatable node);
	
    /**
     * Calculate the current flow through a node (with less than 2 connection, cable and junction only!)
     * 
     * @param node The ISESimulatable instance
     * @return the magnitude of the current, in amps
     */
	double getCurrentMagnitude(ISESimulatable Tile);
	
	ISESubComponent newComponent(TileEntity dataProviderTileEntity);
	
	ISESubComponent newComponent(ISEComponentDataProvider dataProvider, TileEntity parent);
	
	ISESimulatable newCable(TileEntity dataProviderTileEntity, boolean isGridInterConnectionPoint);
	
    /**
     * Add a TileEntity to the energyNet
     */
    void attachTile(TileEntity te);

    void markTileForUpdate(TileEntity te);

    void detachTile(TileEntity te);

    void reattachTile(TileEntity te);
    
    void attachGridObject(World world, int x, int y, int z, byte type);
    
    void detachGridObject(World world, int x, int y, int z);
    
    void connectGridNode(World world, int x1, int y1, int z1, int x2, int y2, int z2, double resistance);
    
    void breakGridConnection(World world, int x1, int y1, int z1, int x2, int y2, int z2);
}
