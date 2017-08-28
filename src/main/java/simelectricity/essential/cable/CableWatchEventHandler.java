package simelectricity.essential.cable;

import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkWatchEvent.Watch;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import simelectricity.essential.api.ISEGenericCable;

public class CableWatchEventHandler {
    @SubscribeEvent
    public void onChunkWatchEvent(Watch event) {
        Chunk chunk = event.getPlayer().world.getChunkFromChunkCoords(event.getChunk().x, event.getChunk().z);

        for (Object tileEntity : chunk.getTileEntityMap().values()) {
            if (tileEntity instanceof ISEGenericCable)
                ((ISEGenericCable) tileEntity).onCableRenderingUpdateRequested();
        }
    }
}
