package simElectricity.API.Internal;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import simElectricity.API.DataProvider.ISEComponentDataProvider;
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.EnergyTile.ISESubComponent;
import simElectricity.Common.EnergyNet.Events.GridConnectionEvent;
import simElectricity.Common.EnergyNet.Events.GridDisconnectionEvent;
import simElectricity.Common.EnergyNet.Events.GridObjectAttachEvent;
import simElectricity.Common.EnergyNet.Events.GridObjectDetachEvent;
import simElectricity.Common.EnergyNet.Events.TileAttachEvent;
import simElectricity.Common.EnergyNet.Events.TileChangeEvent;
import simElectricity.Common.EnergyNet.Events.TileDetachEvent;
import simElectricity.Common.EnergyNet.Events.TileRejoinEvent;

public interface IEnergyNetAgent {
    /**
     * Return the voltage of a ISESimulatable instance, RELATIVE TO GROUND!
     * 
     * @param node The ISESimulatable instance
     * @return the voltage of the node, in volts
     */
	public double getVoltage(ISESimulatable node);
	
    /**
     * Calculate the current flow through a node (with less than 2 connection, cable and junction only!)
     * 
     * @param node The ISESimulatable instance
     * @return the magnitude of the current, in amps
     */
	public double getCurrentMagnitude(ISESimulatable Tile);
	
	public ISESubComponent newComponent(TileEntity dataProviderTileEntity);
	
	public ISESubComponent newComponent(ISEComponentDataProvider dataProvider, TileEntity parent);
	
	public ISESimulatable newCable(TileEntity dataProviderTileEntity);
	
    public void attachTile(TileEntity te);

    public void markTileForUpdate(TileEntity te);

    public void detachTile(TileEntity te);

    public void reattachTile(TileEntity te);
    
    public void attachGridObject(World world, int x, int y, int z, byte type);
    
    public void detachGridObject(World world, int x, int y, int z);
    
    public void connectGridNode(World world, int x1, int y1, int z1, int x2, int y2, int z2, double resistance);
    
    public void breakGridConnection(World world, int x1, int y1, int z1, int x2, int y2, int z2);
}
