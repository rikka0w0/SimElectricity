package simelectricity.api.internal;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import simelectricity.api.components.ISEComponentParameter;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.node.ISESimulatable;
import simelectricity.api.node.ISESubComponent;

/**
 * Provides access to the SimElectricity EnergyNet, see {@link simelectricity.api.SEAPI}
 * <p/>
 * DO NOT implement this else where, internal only!
 * @author rikka0_0
 */
public interface ISEEnergyNetAgent {
    /**
     * Check if a TileEntity can be connected from the given direction
     * @param tileEntity
     * @param direction
     * @return true if the connection can be made
     */
    boolean canConnectTo(TileEntity tileEntity, Direction direction);

    /**
     * Create a new component, WITHOUT register it.
     * Normally this should be called in the constructor of the host TileEntity.
     * @param dataProvider The class which provides electrical parameter of the component, can be the same as {@param parent}
     *                     dataProvider should implement one of the interfaces in {@link simelectricity.api.components} except
     *                     {@link simelectricity.api.components.ISECable} and {@link simelectricity.api.components.ISEComponentParameter}.
     * @param parent The host TileEntity
     * @return A reference to ISESubComponent, which also implements the corresponding ISEComponentParameter.
     *          TileEntity should store this reference and use it to access simulation results, e.g. voltage.
     */
    ISESubComponent<?> newComponent(ISEComponentParameter dataProvider, TileEntity parent);

    /**
     * Create a new cable component, WITHOUT register it.
     * Normally this should be called in the constructor of the host TileEntity.
     * @param dataProviderTileEntity The host TileEntity, and it has to provide electrical parameter of the cable.
     *                               (Implement {@link simelectricity.api.components.ISECable})
     * @param isGridInterConnectionPoint Indicates whether the EnergyNet should attempt to connect this cable node to the grid node
     * @return A reference to ISESimulatable, which also implements the corresponding ISECable.
     *          Cable TileEntity should store this reference and use it to access simulation results, e.g. voltage.
     */
    ISESimulatable newCable(TileEntity dataProviderTileEntity, boolean isGridInterConnectionPoint);

    /**
     * Create a grid node at given location, WITHOUT register it.
     * @param pos
     * @param numOfParallelConductor the number of parallel conductors, e.g. number of phases.
     *                               GridNodes with different conductor number cannot be connected together.
     * @return A reference to ISEGridNode. The {@link simelectricity.api.tile.ISEGridTile} should store this reference
     *          and use it to access simulation results, e.g. voltage.
     */
    ISEGridNode newGridNode(BlockPos pos, int numOfParallelConductor);

    /**
     * Attempt to locate a existing grid node at the given worla and given location
     * @param world
     * @param pos
     * @return the grid node, null if not applicable
     */
    ISEGridNode getGridNodeAt(World world, BlockPos pos);

    /**
     * @param world the world that contains the node/the host of the node
     * @param node
     * @return Check if the given node is valid. e.g. can be used for simulation
     */
    boolean isNodeValid(World world, ISESimulatable node);




    /**
     * Add and register a TileEntity to the energyNet.
     * @param te must implement one interface from {@link simelectricity.api.tile}
     */
    void attachTile(TileEntity te);
    /**
     * Notify the EnergyNet that the parameter of one or more of component within the TileEntity has changed.
     * @param te must implement one interface from {@link simelectricity.api.tile}
     */
    void updateTileParameter(TileEntity te);
    /**
     * Remove a TileEntity from the energyNet.
     * @param te must implement one interface from {@link simelectricity.api.tile}
     */
    void detachTile(TileEntity te);
    /**
     * Notify the EnergyNet that the connection or orientation of one or more of component within the TileEntity has changed.
     * E.g. the machine has been rotated, a new cover panel has blocked an existing connection, e.t.c...
     * @param te must implement one interface from {@link simelectricity.api.tile}
     */
    void updateTileConnection(TileEntity te);
    /**
     * Add and register a grid node to the EnergyNet
     */
    void attachGridNode(World world, ISEGridNode node);
    /**
     * Remove a grid node from the EnergyNet
     */
    void detachGridNode(World world, ISEGridNode node);
    /**
     * Connect two grid nodes with a given resistor (cable),
     * the effect is persisted and changes will be automatically saved with the world.
     * @param world the world which has the grid node
     * @param node1
     * @param node2
     * @param resistance The exact electrical resistance between two nodes, in Ohms.
     */
    void connectGridNode(World world, ISEGridNode node1, ISEGridNode node2, double resistance);
    /**
     * Cut the connection between two grid node,
     * the effect is persisted and changes will be automatically saved with the world.
     */
    void breakGridConnection(World world, ISEGridNode node1, ISEGridNode node2);
    /**
     * Connect two grid nodes with a given resistor (cable),
     * the effect is persisted and changes will be automatically saved with the world.
     * @param world the world which has the grid node
     * @param primary the primary grid node
     * @param secondary the secondary grid node
     * @param resistance The winding equivalent resistance, refer to secondary, in Ohms.
     * @param ratio The turns ratio between primary and secondary, >1 for step-up, <1 for step-down
     */
    void makeTransformer(World world, ISEGridNode primary, ISEGridNode secondary, double resistance, double ratio);
    /**
     * Remove the grid transformer connected between two grid node,
     * the effect is persisted and changes will be automatically saved with the world.
     * @param world the world which has the grid node
     * @param node Either primary or secondary grid node
     */
    void breakTransformer(World world, ISEGridNode node);
    
    /**
     * @return How many RF equal to 1 Joule
     */
    double joule2rf();
}
