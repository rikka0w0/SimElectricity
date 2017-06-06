package simelectricity.essential.common;

import simelectricity.api.SEAPI;

public abstract class SEEnergyTile extends SETileEntity{
    protected boolean isAddedToEnergyNet;
    
    /**
     * Called just before joining the energyNet, do some initialization here
     */
    public void onLoad() {
    }

    /**
     * Called just before detaching from the energyNet
     */    
    public void onUnload() {
    }
	
	@Override
    public void updateEntity() {
        super.updateEntity();
               	
        if (!worldObj.isRemote && !isAddedToEnergyNet) {
            onLoad();
            SEAPI.energyNetAgent.attachTile(this);
            this.isAddedToEnergyNet = true;
        }
    }

    @Override
    public void invalidate() {
    	super.invalidate();
    	
        if (!worldObj.isRemote && isAddedToEnergyNet) {
            onUnload();
            SEAPI.energyNetAgent.detachTile(this);
            this.isAddedToEnergyNet = false;
        }
    }
	
    @Override
    public void onChunkUnload(){
    	invalidate();
    }
}
