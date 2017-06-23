package simelectricity.essential.cable;

import simelectricity.essential.api.ISEGenericCable;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkWatchEvent;

public class CableWatchEventHandler {
    @SubscribeEvent
    public void onChunkWatchEvent(ChunkWatchEvent.Watch event) {
        Chunk chunk = event.player.worldObj.getChunkFromChunkCoords(event.chunk.chunkXPos, event.chunk.chunkZPos);

        for (Object tileEntity : chunk.chunkTileEntityMap.values()) {
        	if (tileEntity instanceof ISEGenericCable)
        		((ISEGenericCable) tileEntity).onCableRenderingUpdateRequested();
        }
	}
}
