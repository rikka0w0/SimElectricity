package simelectricity.energynet.components;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import simelectricity.api.components.ISECable;

public class Cable extends CableBase<ISECable> implements ISECable {
    //Properties, do not modify their value!
    public final boolean isGridInterConnectionPoint;
    
    private volatile boolean isGridLinkEnabled;
    private volatile int color;
    private volatile boolean[] canConnectOnSide;        //Use canConnectOnSide() instead
    
    //Simulation & Optimization
    public volatile GridNode connectedGridNode;

    public Cable(ISECable dataProvider, BlockEntity te, boolean isGridInterConnectionPoint) {
        super(dataProvider, te);
        this.isGridInterConnectionPoint = isGridInterConnectionPoint;
        
        this.connectedGridNode = null;
    }

    @Override
    public synchronized void updateComponentParameters() {
        super.updateComponentParameters();
        color = this.dataProvider.getColor();
        isGridLinkEnabled = this.dataProvider.isGridLinkEnabled();


        canConnectOnSide = new boolean[6];
        int i = 0;
        for (Direction dir : Direction.values()) {
            canConnectOnSide[i] = this.dataProvider.canConnectOnSide(dir);
            i++;
        }
    }

    /////////////////////////
    ///ISECableParameter
    /////////////////////////
    @Override
    public synchronized boolean canConnectOnSide(Direction direction) {
    	if (canConnectOnSide == null)
    		return false;
        
    	return canConnectOnSide[direction.ordinal()];
    }

    @Override
    public synchronized int getColor() {
        return this.color;
    }

    @Override
    public synchronized boolean isGridLinkEnabled() {
        return this.isGridLinkEnabled;
    }


}
