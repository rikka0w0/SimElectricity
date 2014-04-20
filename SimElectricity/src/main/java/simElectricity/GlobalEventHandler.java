package simElectricity;

import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;

public class GlobalEventHandler {
	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		WorldData.onWorldUnload(event.world);
	}
	
	@SubscribeEvent
	public void tick(WorldTickEvent event){
		if(event.phase!=Phase.START)
			return;
		if(event.side!=Side.SERVER)
			return;	
		
		EnergyNet.onTick(event.world);
	}
}
