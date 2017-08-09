package simelectricity.essential.cable;

import simelectricity.essential.api.ISEGenericCable;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkWatchEvent;

public class CableWatchEventHandler {
    @SubscribeEvent
    public void onChunkWatchEvent(ChunkWatchEvent.Watch event) {
        Chunk chunk = event.getPlayer().world.getChunkFromChunkCoords(event.getChunk().chunkXPos, event.getChunk().chunkZPos);

        for (Object tileEntity : chunk.getTileEntityMap().values()) {
        	if (tileEntity instanceof ISEGenericCable)
        		((ISEGenericCable) tileEntity).onCableRenderingUpdateRequested();
        }
	}
}
