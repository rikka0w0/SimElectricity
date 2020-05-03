package simelectricity.essential;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.IForgeRegistry;
import rikka.librikka.block.ICustomBoundingBox;
import simelectricity.essential.api.ISEChunkWatchSensitiveTile;
import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.client.ModelDataProvider;
import simelectricity.essential.coverpanel.CoverPanelRegistry;
import simelectricity.essential.coverpanel.SECoverPanelFactory;
import simelectricity.essential.utils.network.MessageContainerSync;


@Mod(Essential.MODID)
public class Essential {
    public static final String MODID = "sime_essential";

    @Deprecated
    public static CommonProxy proxy = DistExecutor.runForDist(()->()->new ClientProxy(), ()->()->new CommonProxy());

    public static Essential instance;

	private static final String PROTOCOL_VERSION = "1";
    public SimpleChannel networkChannel;
    
    public Essential() {
    	if (instance == null) 
            instance = this;
        else
            throw new RuntimeException("Duplicated Class Instantiation: simelectricity.essential.Essential");
    	
    	ConfigProvider.register();
    	
    	SEEAPI.coverPanelRegistry = CoverPanelRegistry.INSTANCE;
    }
    
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public final static class ModEventBusHandler {
    	@SubscribeEvent
    	public static void gatherData(GatherDataEvent event) {
    		DataGenerator generator = event.getGenerator();
    		ExistingFileHelper exfh = event.getExistingFileHelper();
    		if (event.includeServer()) {

    		}
    		if (event.includeClient()) {
    			generator.addProvider(new ModelDataProvider(generator, exfh));
    		}
    	}
    	
    	@SubscribeEvent
		public static void registerBlocks(final RegistryEvent.Register<Block> event) {
    		IForgeRegistry registry = event.getRegistry();
    		BlockRegistry.initBlocks();
        	BlockRegistry.registerBlocks(registry, false);
    	}
    	
    	@SubscribeEvent
		public static void registerItems(final RegistryEvent.Register<Item> event) {
    		IForgeRegistry registry = event.getRegistry();
    		ItemRegistry.initItems();
        	BlockRegistry.registerBlocks(registry, true);
            ItemRegistry.registerItems(registry);
    	}
    	
    	@SubscribeEvent
    	public static void onTileEntityTypeRegistration(final RegistryEvent.Register<TileEntityType<?>> event) {
    		BlockRegistry.registerTileEntities(event.getRegistry());
    	}
    	
    	@SubscribeEvent
    	public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
    		BlockRegistry.registerContainers(event.getRegistry());
    	}
    	
    	@SubscribeEvent
    	public static void onCommonSetup(FMLCommonSetupEvent event) {
    		Essential.instance.networkChannel = NetworkRegistry.newSimpleChannel(
    			    new ResourceLocation(MODID, "network_channel"),
    			    () -> PROTOCOL_VERSION,
    			    PROTOCOL_VERSION::equals,
    			    PROTOCOL_VERSION::equals
    			);
    		
    		Essential.instance.networkChannel.registerMessage(0, 
    				MessageContainerSync.class, MessageContainerSync.processor::toBytes, MessageContainerSync.processor::fromBytes, 
    				MessageContainerSync.processor::handler);

        	SEEAPI.coverPanelRegistry.registerCoverPanelFactory(new SECoverPanelFactory());
    	}
    }
    
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public final static class ForgeEventBusHandler {
		@SubscribeEvent
		public static void onChunkWatchEvent(ChunkWatchEvent.Watch event) {
			Chunk chunk = event.getPlayer().world.getChunk(event.getPos().x, event.getPos().z);

			for (Object tileEntity : chunk.getTileEntityMap().values()) {
				if (tileEntity instanceof ISEChunkWatchSensitiveTile)
					((ISEChunkWatchSensitiveTile) tileEntity).onRenderingUpdateRequested();
			}
		}
		
		@SubscribeEvent
		public static void onBlockHighLight(DrawHighlightEvent.HighlightBlock event) {
			ICustomBoundingBox.onBlockHighLight(event);
		}
    }
}
