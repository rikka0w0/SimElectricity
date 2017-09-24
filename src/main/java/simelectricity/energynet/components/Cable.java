package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import simelectricity.api.components.ISECable;
import simelectricity.api.tile.ISECableTile;
import simelectricity.energynet.components.SEComponent.Tile;

public class Cable extends Tile<ISECable> implements ISECable {
    //Properties, do not modify their value!
    public final boolean isGridInterConnectionPoint;
    
    private volatile boolean isGridLinkEnabled;
    private volatile int color;
    private volatile double resistance;
    private volatile boolean hasShuntResistance;
    private volatile double shuntResistance;
    private volatile boolean[] canConnectOnSide;        //Use canConnectOnSide() instead
    
    //Simulation & Optimization
    public volatile GridNode connectedGridNode;

    public Cable(ISECableTile dataProvider, TileEntity te, boolean isGridInterConnectionPoint) {
        super(dataProvider, te);
        this.isGridInterConnectionPoint = isGridInterConnectionPoint;
        
        this.connectedGridNode = null;
    }

    @Override
    public synchronized void updateComponentParameters() {
        color = this.dataProvider.getColor();
        resistance = this.dataProvider.getResistance();
        isGridLinkEnabled = this.dataProvider.isGridLinkEnabled();
        hasShuntResistance = this.dataProvider.hasShuntResistance();
        shuntResistance = this.dataProvider.getShuntResistance();

        canConnectOnSide = new boolean[6];
        int i = 0;
        for (EnumFacing dir : EnumFacing.VALUES) {
            canConnectOnSide[i] = this.dataProvider.canConnectOnSide(dir);
            i++;
        }
    }

    /////////////////////////
    ///ISECableParameter
    /////////////////////////
    @Override
    public synchronized boolean canConnectOnSide(EnumFacing direction) {
    	if (canConnectOnSide == null)
    		return false;
        
    	return canConnectOnSide[direction.ordinal()];
    }

    @Override
    public synchronized int getColor() {
        return this.color;
    }

    @Override
    public synchronized double getResistance() {
        return this.resistance;
    }

    @Override
    public synchronized boolean isGridLinkEnabled() {
        return this.isGridLinkEnabled;
    }

    @Override
    public synchronized boolean hasShuntResistance() {
        return this.hasShuntResistance;
    }

    @Override
    public synchronized double getShuntResistance() {
        return this.shuntResistance;
    }
}
