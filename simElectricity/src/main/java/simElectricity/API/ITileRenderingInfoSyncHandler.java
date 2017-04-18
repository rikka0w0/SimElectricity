package simElectricity.API;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkWatchEvent;

public interface ITileRenderingInfoSyncHandler {
	/**
	 * Send a sync. packet to client immediately, to update client rendering
	 * </p>
	 * This method will be called when the chunk containing the tileEntity is seen by a player
	 */
	public void sendRenderingInfoToClient();
	
	public static class ForgeEventHandler{
		public ForgeEventHandler(){
			MinecraftForge.EVENT_BUS.register(this);
		}
		
	    @SubscribeEvent
	    public void onChunkWatchEvent(ChunkWatchEvent.Watch event) {
	        Chunk chunk = event.player.worldObj.getChunkFromChunkCoords(event.chunk.chunkXPos, event.chunk.chunkZPos);

	        for (Object tileEntity : chunk.chunkTileEntityMap.values()) {
	        	if (tileEntity instanceof ITileRenderingInfoSyncHandler)
	        		((ITileRenderingInfoSyncHandler) tileEntity).sendRenderingInfoToClient();
	        }
		}
	}
}
