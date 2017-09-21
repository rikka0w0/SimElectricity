package simelectricity;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import simelectricity.api.SEAPI;

@Mod.EventBusSubscriber(modid = SimElectricity.MODID, value = Side.CLIENT)
public class ClientRegistrationHandler {
	@SubscribeEvent
	public static void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(SEAPI.managementToolItem, 0,
                new ModelResourceLocation(SEAPI.managementToolItem.getRegistryName(), "inventory"));
	}
}
