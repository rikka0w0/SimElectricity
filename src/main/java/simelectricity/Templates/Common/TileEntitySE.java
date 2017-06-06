package simelectricity.Templates.Common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import simelectricity.api.SEAPI;


public abstract class TileEntitySE extends TileEntity{
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
            SEAPI.energyNetAgent.attachTile(this);
            this.isAddedToEnergyNet = true;
        }
    }

    @Override
    public void invalidate() {
    	super.invalidate();
    	
        if (!worldObj.isRemote && isAddedToEnergyNet && attachToEnergyNet()) {
            onUnload();
            SEAPI.energyNetAgent.detachTile(this);
            this.isAddedToEnergyNet = false;
        }
    }
	
    @Override
    public void onChunkUnload(){
    	invalidate();
    }
    
    
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
	public void markTileEntityForS2CSync(){
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@SideOnly(value = Side.CLIENT)
	protected void markForRenderUpdate(){
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
    
    
	//Sync
	public void prepareS2CPacketData(NBTTagCompound nbt) {}
	
	@SideOnly(value = Side.CLIENT)
	public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
		markForRenderUpdate();
	}
    
    @Override
	public Packet getDescriptionPacket(){
		//System.out.println("[DEBUG]:Server sent tile sync packet");
		NBTTagCompound tagCompound = new NBTTagCompound();
		prepareS2CPacketData(tagCompound);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tagCompound);
    }
    
    @Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
		//System.out.println("[DEBUG]:Client recived tile sync packet");
		if (worldObj.isRemote)
			onSyncDataFromServerArrived(pkt.func_148857_g());
    }
}
