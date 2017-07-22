package simelectricity.essential.common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public abstract class SETileEntity extends TileEntity{
    @Override
    public void onChunkUnload(){
    	invalidate();
    }
	
	/**
	 * As mentioned in documentations, developers spotted that Vanilla MineCraft tends to recreate the tileEntity when the blockState changes
	 * Being a modder, most of us don't want this. The following method tweaks the vanilla behavior and gives you the original behavior.
	 */
	/*
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return (oldState.getBlock() != newState.getBlock());	//Was "return !isVanilla || (oldBlock != newBlock);" in 1.7.10
	}
	*/
    
	protected void markTileEntityForS2CSync(){
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		//worldObj.notifyBlockUpdate(getPos(), worldObj.getBlockState(getPos()), worldObj.getBlockState(getPos()), 2);
	}
	
	@SideOnly(value = Side.CLIENT)
	protected void markForRenderUpdate(){
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		//worldObj.notifyBlockUpdate(getPos(), worldObj.getBlockState(getPos()), worldObj.getBlockState(getPos()), 1);
	}
	
	//Sync
	public void prepareS2CPacketData(NBTTagCompound nbt) {}
	
	@SideOnly(value = Side.CLIENT)
	public void onSyncDataFromServerArrived(NBTTagCompound nbt) {}
	
	@Override
	public Packet getDescriptionPacket()
    //public S35PacketUpdateTileEntity getUpdatePacket()
    {
		//System.out.println("[DEBUG]:Server sent tile sync packet");
		NBTTagCompound tagCompound = new NBTTagCompound();
		prepareS2CPacketData(tagCompound);
		//return new SPacketUpdateTileEntity(pos, 1, tagCompound);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tagCompound);
    }

	@Override
	public void onDataPacket(net.minecraft.network.NetworkManager net, S35PacketUpdateTileEntity pkt)
	//public void onDataPacket(net.minecraft.network.NetworkManager net, SPacketUpdateTileEntity pkt)
    {	
		
		if (worldObj.isRemote){
			//System.out.println("[DEBUG]:Client recived tile sync packet");	//Debug
			
			//This is supposed to be Client ONLY!
			//SPacketUpdateTileEntity starts with S, means that this packet is sent from server to client
			//onSyncDataFromServerArrived(pkt.getNbtCompound());
			onSyncDataFromServerArrived(pkt.func_148857_g());
		}
    }
}
