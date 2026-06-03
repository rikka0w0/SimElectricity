package simelectricity.essential;

import net.minecraft.world.level.block.Block;
import net.minecraft.data.DataGenerator;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.NeoForge;
import rikka.librikka.gui.AutoGuiHandler;
import rikka.librikka.model.loader.TERHelper;
import simelectricity.essential.client.BuiltInModelLoader;
import simelectricity.essential.client.ModelDataProvider;
import simelectricity.essential.client.cable.CableModelLoader;
import simelectricity.essential.client.coverpanel.LedPanelRender;
import simelectricity.essential.client.coverpanel.SupportRender;
import simelectricity.essential.client.coverpanel.VoltageSensorRender;
import simelectricity.essential.client.semachine.SEMachineModelLoader;
import simelectricity.essential.client.semachine.SocketRender;
import simelectricity.essential.coverpanel.CoverPanelRegistry;
import simelectricity.essential.client.grid.pole.ConcretePole35kVBER;
import simelectricity.essential.client.grid.pole.ConcretePoleBER;
import simelectricity.essential.client.grid.pole.MetalPole35kVBottomBER;
import simelectricity.essential.client.grid.pole.MetalPole35kVBER;
import simelectricity.essential.client.grid.transformer.PowerTransformerBER;
import simelectricity.essential.client.grid.PowerPoleBER;
import simelectricity.essential.client.grid.GridRenderMonitor;
import simelectricity.essential.grid.BlockEntityPoleBranch;
import simelectricity.essential.grid.BlockEntityPoleConcrete;
import simelectricity.essential.grid.BlockEntityPoleConcrete35kV;
import simelectricity.essential.grid.BlockEntityPoleMetal35kV;
import simelectricity.essential.grid.transformer.BlockEntityDistributionTransformer;
import simelectricity.essential.grid.transformer.BlockEntityPowerTransformerPlaceHolder;
import simelectricity.essential.grid.transformer.BlockEntityPowerTransformerWinding;

@EventBusSubscriber(value = Dist.CLIENT, modid = Essential.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ClientRegistrationHandler {
	public static void registerTileEntityRenders() {
		// ConcretePole35kV
		TERHelper.bind(BlockEntityPoleConcrete35kV.class, ConcretePole35kVBER::new);

		// MetalPole35kV
		TERHelper.bind(BlockEntityPoleMetal35kV.class, MetalPole35kVBER::new);
		TERHelper.bind(BlockEntityPoleMetal35kV.Bottom.class, MetalPole35kVBottomBER::new);

		// ConcretePole
		TERHelper.bind(BlockEntityPoleConcrete.Pole10Kv.Type0.class, ConcretePoleBER::new);
		TERHelper.bind(BlockEntityPoleConcrete.Pole10Kv.Type1.class, ConcretePoleBER::new);
		TERHelper.bind(BlockEntityPoleConcrete.Pole415vType0.class, ConcretePoleBER::new);
		TERHelper.bind(BlockEntityPoleBranch.Type10kV.class, ConcretePoleBER::new);
		TERHelper.bind(BlockEntityPoleBranch.Type415V.class, ConcretePoleBER::new);

		// PowerTransformer
		TERHelper.bind(BlockEntityPowerTransformerPlaceHolder.Render.class, PowerTransformerBER::new);
		TERHelper.bind(BlockEntityPowerTransformerWinding.Primary.class, PowerPoleBER::new);
		TERHelper.bind(BlockEntityPowerTransformerWinding.Secondary.class, PowerPoleBER::new);

		TERHelper.bind(BlockEntityDistributionTransformer.Pole10kV.class, PowerPoleBER::new);
		TERHelper.bind(BlockEntityDistributionTransformer.Pole415V.class, PowerPoleBER::new);
	}

	@SubscribeEvent
	public static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
		event.register(SEMachineModelLoader.id, SEMachineModelLoader.instance);
		event.register(CableModelLoader.id, CableModelLoader.instance);
		event.register(BuiltInModelLoader.id, BuiltInModelLoader.instance);
	}

	@SubscribeEvent
	public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
		event.register(new net.minecraft.client.resources.model.ModelResourceLocation(PowerTransformerBER.modelResLoc, "standalone"));
	}

	@SubscribeEvent
	public static void onModelBake(ModelEvent.BakingCompleted event) {
		SocketRender.INSTANCE.onModelBakeEvent();
		SupportRender.INSTANCE.onModelBakeEvent();
		VoltageSensorRender.instance.onModelBakeEvent();
		LedPanelRender.instance.onModelBakeEvent();

		PowerPoleBER.onModelBakeEvent();
		PowerTransformerBER.onModelBakeEvent();
	}

	@SubscribeEvent
	public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener((net.minecraft.server.packs.resources.ResourceManagerReloadListener) resourceManager -> {
			GridRenderMonitor.instance.markLoadedPowerPoleForRenderingUpdate();
		});
	}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event){
		// Register Gui
		BlockRegistry.registeredGuiContainers.forEach(AutoGuiHandler::registerContainerGui);

    	ClientRegistrationHandler.registerTileEntityRenders();

		NeoForge.EVENT_BUS.register(GridRenderMonitor.instance);
	}

	@SubscribeEvent
	public static void registerBlockColors(net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Block event) {
		for (Block block : CoverPanelRegistry.INSTANCE.getColoredBlocks()) {
			event.register(simelectricity.essential.client.coverpanel.BlockColorHandler.colorHandler, block);
		}
	}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper exfh = event.getExistingFileHelper();

		if (event.includeClient()) {
			generator.addProvider(true, (net.minecraft.data.DataProvider.Factory<ModelDataProvider>) output -> new ModelDataProvider(output, exfh));
		}
	}
}
