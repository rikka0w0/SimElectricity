package simElectricity.API.Internal;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import simElectricity.API.DataProvider.ISEComponentDataProvider;
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.EnergyTile.ISESubComponent;

/**
 * Provides necessary functions which enable access to the SimElectricity EnergyNet
 * @author rikka0w0
 */
public interface IEnergyNetAgent {
    /**
     * @param circuit node
     * @return the voltage of the node, in volts, ground referenced
     */
	double getVoltage(ISESimulatable node);
	
    /**
     * @param node The ISESimulatable instance
     * @return the magnitude (Absolute value) of the current flow through the the cable or switch, in amps
     */
	double getCurrentMagnitude(ISESimulatable Tile);
	
	boolean canConnectTo(TileEntity tileEntity, ForgeDirection direction);
	
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
