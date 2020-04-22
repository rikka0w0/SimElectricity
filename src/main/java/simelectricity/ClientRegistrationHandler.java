package simelectricity;

//import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.registries.ForgeRegistries;
//import simelectricity.api.SEAPI;

//@Mod.EventBusSubscriber(modid = SimElectricity.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistrationHandler {
	@SubscribeEvent
	public static void registerModel(ModelRegistryEvent event) {
		// TO-DO Fix this!
		return;//Modelbakery::processLoading loadBlockstate
		//field_217848_D ResourceLocation to be load
		// field_217849_F loaded ResourceLocation
		//IUnbakedModel
		//Minecraft.getInstance().getItemRenderer().getItemModelMesher().register();
//        ModelLoader.setCustomModelResourceLocation(SEAPI.managementToolItem, 0,
//                new ModelResourceLocation(SEAPI.managementToolItem.getRegistryName(), "inventory"));
	}
}
