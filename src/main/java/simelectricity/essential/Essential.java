package simelectricity.essential;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.fml.relauncher.Side;
import rikka.librikka.AutoGuiHandler;
import simelectricity.SimElectricity;
import simelectricity.essential.api.ISEChunkWatchSensitiveTile;
import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.coverpanel.CoverPanelRegistry;
import simelectricity.essential.coverpanel.SECoverPanelFactory;
import simelectricity.essential.utils.network.MessageContainerSync;


@Mod(modid = Essential.MODID, name = "SimElectricity Essential", dependencies = "required-after:"+ SimElectricity.MODID)
public class Essential {
    public static final String MODID = "sime_essential";

    @SidedProxy(clientSide = "simelectricity.essential.ClientProxy", serverSide = "simelectricity.essential.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(Essential.MODID)
    public static Essential instance;

    public SimpleNetworkWrapper networkChannel;
        
    /**
     * PreInitialize
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	new ConfigProvider(event.getSide().isClient());
    	
        SEEAPI.coverPanelRegistry = new CoverPanelRegistry();

        networkChannel = NetworkRegistry.INSTANCE.newSimpleChannel(Essential.MODID);
        networkChannel.registerMessage(MessageContainerSync.HandlerClient.class, MessageContainerSync.class, 0, Side.CLIENT);
        networkChannel.registerMessage(MessageContainerSync.HandlerServer.class, MessageContainerSync.class, 1, Side.SERVER);
        
        proxy.preInit();
    }

    @Mod.EventBusSubscriber(modid = Essential.MODID)
    public static class RegistrationHandler {
    	@SubscribeEvent
		public static void registerBlocks(RegistryEvent.Register<Block> event) {
    		IForgeRegistry registry = event.getRegistry();
    		BlockRegistry.initBlocks();
        	BlockRegistry.registerBlocks(registry, false);
    	}
    	
    	@SubscribeEvent
		public static void registerItems(RegistryEvent.Register<Item> event) {
    		IForgeRegistry registry = event.getRegistry();
    		ItemRegistry.initItems();
        	BlockRegistry.registerBlocks(registry, true);
            ItemRegistry.registerItems(registry);
    	}
    }
    
    
    /**
     * Initialize
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        BlockRegistry.registerTileEntities();

        proxy.init();

        MinecraftForge.EVENT_BUS.register(new Object() {
            @SubscribeEvent
            public void onChunkWatchEvent(ChunkWatchEvent.Watch event) {
                Chunk chunk = event.getPlayer().world.getChunkFromChunkCoords(event.getChunk().x, event.getChunk().z);

                for (Object tileEntity : chunk.getTileEntityMap().values()) {
                    if (tileEntity instanceof ISEChunkWatchSensitiveTile)
                        ((ISEChunkWatchSensitiveTile) tileEntity).onRenderingUpdateRequested();
                }
            }
        });

        //Register GUI handler
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new AutoGuiHandler());
    }

    /**
     * PostInitialize
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	proxy.postInit();
        SEEAPI.coverPanelRegistry.registerCoverPanelFactory(new SECoverPanelFactory());
    }
}
