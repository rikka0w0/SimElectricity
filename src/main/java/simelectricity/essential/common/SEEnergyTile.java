package simelectricity.essential.common;

import simelectricity.api.SEAPI;

public abstract class SEEnergyTile extends SETileEntity{
    protected boolean isAddedToEnergyNet;
    
    /**
     * Called just before joining the energyNet, do some initialization here </p>
     * Should not use onLoad() in client anyway!
     */
    public void onLoad() {
        if (!world.isRemote && !isAddedToEnergyNet) {
            SEAPI.energyNetAgent.attachTile(this);
            this.isAddedToEnergyNet = true;
        }
    }

    @Override
    public void invalidate() {
    	super.invalidate();
    	
        if (!world.isRemote && isAddedToEnergyNet) {
            SEAPI.energyNetAgent.detachTile(this);
            this.isAddedToEnergyNet = false;
        }
    }
	
    @Override
    public void onChunkUnload(){
    	invalidate();
    }
}
