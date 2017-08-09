package simelectricity.essential.common;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class SETileEntity extends TileEntity{
    @Override
    public void onChunkUnload(){
    	invalidate();
    }
	
	/**
	 * As mentioned in documentations, developers spotted that Vanilla MineCraft
	 * tends to recreate the tileEntity when the blockState changes
	 * 
	 * Being a modder, most of us don't want this.
	 * The following method tweaks the vanilla behavior and gives you the original behavior.
	 */
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return (oldState.getBlock() != newState.getBlock());	//Was "return !isVanilla || (oldBlock != newBlock);" in 1.7.10
	}
	
    
	protected void markTileEntityForS2CSync(){
		world.notifyBlockUpdate(getPos(), world.getBlockState(getPos()), world.getBlockState(getPos()), 2);
	}
	
	@SideOnly(value = Side.CLIENT)
	protected void markForRenderUpdate(){
		world.notifyBlockUpdate(getPos(), world.getBlockState(getPos()), world.getBlockState(getPos()), 1);
	}
	
	//Sync
	public void prepareS2CPacketData(NBTTagCompound nbt) {}
	
	@SideOnly(value = Side.CLIENT)
	public void onSyncDataFromServerArrived(NBTTagCompound nbt) {}
	
	@Override
	public final SPacketUpdateTileEntity getUpdatePacket()
    {
		//System.out.println("[DEBUG]:Server sent tile sync packet");
		NBTTagCompound tagCompound = new NBTTagCompound();
		prepareS2CPacketData(tagCompound);
		return new SPacketUpdateTileEntity(pos, 1, tagCompound);
    }

	@Override
	public final void onDataPacket(net.minecraft.network.NetworkManager net, SPacketUpdateTileEntity pkt)
    {	
		
		if (world.isRemote){
			System.out.println("[DEBUG]:Client recived INDIVIDUAL tileSync packet");	//Debug
			
			//This is supposed to be Client ONLY!
			//SPacketUpdateTileEntity starts with S, means that this packet is sent from server to client
			onSyncDataFromServerArrived(pkt.getNbtCompound());
		}
    }
	
	/**
	 * Chunk Sync
	 */
	@Override
    public final NBTTagCompound getUpdateTag() {
    	NBTTagCompound nbt = super.getUpdateTag();
    	
    	//Prepare custom payload
    	prepareS2CPacketData(nbt);
    	
    	return nbt;
    }
	
    /**
     * Called when the chunk's TE update tag, gotten from {@link #getUpdateTag()}, is received on the client.
     * <p>
     * Used to handle this tag in a special way. By default this simply calls {@link #readFromNBT(NBTTagCompound)}.
     * This function should only be called by the client thread (I think
     * @param tag The {@link NBTTagCompound} sent from {@link #getUpdateTag()}
     */
	@Override
    public void handleUpdateTag(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		if (world.isRemote){
			System.out.println("[DEBUG]:Client recived CHUNK tileSync packet");	//Debug
			
			onSyncDataFromServerArrived(tag);
		}
    }
}
