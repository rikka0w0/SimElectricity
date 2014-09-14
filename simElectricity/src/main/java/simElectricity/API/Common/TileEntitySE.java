package simElectricity.API.Common;

import simElectricity.API.Energy;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntitySE extends TileEntity{
    protected boolean isAddedToEnergyNet;
    protected short overVoltageTick = -1;
    
    /**
     * Continuous over voltage occurs! Oops! Do explosions here! Override this when necessary 0w0
     */
    public void onOverVoltage(){}
    
    /**
     * The maximum allowed ticks for continuous over voltage
     * @return
     */
    public int getMaxOverVoltageTick(){return 15;}
    
    /**
     * Check the input voltage
     * @param voltage The voltage supplied into this machine
     */
    public void checkVoltage(double voltage,double maxVoltage){
        if (voltage > maxVoltage){
        	if (overVoltageTick == -1)
        		overVoltageTick = 0;
        }else{
        	overVoltageTick = -1;
        }
    }
    
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
    
    /**
     * Override this to false to prevent from joining the energyNet automatically
     * @return
     */
    public abstract boolean attachToEnergyNet();
	
	@Override
    public void updateEntity() {
        super.updateEntity();
        
        //No client side operation
        if (worldObj.isRemote)
        	return;
        	
        if (!isAddedToEnergyNet && attachToEnergyNet()) {
            onLoad();
            Energy.postTileAttachEvent(this);
            this.isAddedToEnergyNet = true;
        }
        
    	//Find out continuous over voltage
    	if (overVoltageTick != -1){
    		overVoltageTick++;
    		if (overVoltageTick>getMaxOverVoltageTick()){
    			overVoltageTick = -1;
    			onOverVoltage();
    		}
    	} 	
    }

    @Override
    public void invalidate() {
    	super.invalidate();
    	
        if (!worldObj.isRemote && isAddedToEnergyNet && attachToEnergyNet()) {
            onUnload();
            Energy.postTileDetachEvent(this);
            this.isAddedToEnergyNet = false;
        }
    }
	
    @Override
    public void onChunkUnload(){
    	invalidate();
    }
    
    @Override
	public Packet getDescriptionPacket()
    {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		writeToNBT(nbttagcompound);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, -999, nbttagcompound);
    }
    
    @Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
		super.onDataPacket(net, pkt);
		readFromNBT(pkt.func_148857_g());
		
		if(worldObj.isRemote)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}
