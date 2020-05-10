package simelectricity.extension.facades;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import simelectricity.essential.api.SEEAPI;

@Mod.EventBusSubscriber(modid = Facades.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy {
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event){
        for (Block block: SEEAPI.coloredBlocks) {
            Minecraft.getInstance().getBlockColors().register(BlockColorHandler.colorHandler, block);
        }
    }
}