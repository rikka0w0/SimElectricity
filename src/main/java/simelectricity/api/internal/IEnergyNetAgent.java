package simelectricity.api.internal;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import simelectricity.api.components.ISEComponentParameter;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.node.ISESimulatable;
import simelectricity.api.node.ISESubComponent;

/**
 * Provides necessary functions which enable access to the SimElectricity EnergyNet
 * @author rikka0w0
 */
public interface IEnergyNetAgent {
    /**
     * @return the voltage of the node, in volts, ground referenced
     */
	double getVoltage(ISESimulatable node);
	
	double getCurrentMagnitude(ISESimulatable node);
	
	boolean canConnectTo(TileEntity tileEntity, ForgeDirection direction);
	
	ISESubComponent newComponent(ISEComponentParameter dataProvider, TileEntity parent);
	
	ISESimulatable newCable(TileEntity dataProviderTileEntity, boolean isGridInterConnectionPoint);
	
	ISEGridNode newGridNode(int x, int y, int z);
	
	ISEGridNode getGridNodeAt(World world, int x, int y, int z);

	boolean isNodeValid(World world, ISESimulatable node);
	
	
	
	
	///////////////////////////
	///Events
	///////////////////////////	
    /**
     * Add a TileEntity to the energyNet
     */
    void attachTile(TileEntity te);

    void updateTileParameter(TileEntity te);

    void detachTile(TileEntity te);

    void updateTileConnection(TileEntity te);
    
    void attachGridObject(World world, ISEGridNode node);
    
    void detachGridObject(World world, ISEGridNode node);
    
    void connectGridNode(World world, ISEGridNode node1, ISEGridNode node2, double resistance);
    
    void breakGridConnection(World world, ISEGridNode node1, ISEGridNode node2);
    
    void makeTransformer(World world, ISEGridNode primary, ISEGridNode secondary, double ratio, double resistance);
    
    void breakTransformer(World world, ISEGridNode node);
}
