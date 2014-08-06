package simElectricity.API.Common;

import simElectricity.API.Energy;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

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
        if (!worldObj.isRemote && !isAddedToEnergyNet && attachToEnergyNet()) {
            onLoad();
            Energy.postTileAttachEvent(this);
            this.isAddedToEnergyNet = true;
        }
    }

    @Override
    public void invalidate() {
        if (!worldObj.isRemote && isAddedToEnergyNet && attachToEnergyNet()) {
            onUnload();
            Energy.postTileDetachEvent(this);
            this.isAddedToEnergyNet = false;
        }

        super.invalidate();
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
