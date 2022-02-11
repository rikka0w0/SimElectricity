package simelectricity.essential;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import rikka.librikka.blockentity.BlockEntityHelper;
import simelectricity.essential.api.ISEChunkWatchSensitiveTile;
import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.client.ClientConfigs;
import simelectricity.essential.coverpanel.CoverPanelRegistry;
import simelectricity.essential.coverpanel.SECoverPanelFactory;
import simelectricity.essential.utils.network.MessageContainerSync;

@Mod(Essential.MODID)
public class Essential {
    public static final String MODID = "sime_essential";

    public static CommonProxy proxy = DistExecutor.runForDist(()->()->new ClientProxy(), ()->()->new CommonProxy());

    public static Essential instance;

	private static final String PROTOCOL_VERSION = "1";
    public SimpleChannel networkChannel;

    public Essential() {
    	if (instance == null)
            instance = this;
        else
            throw new RuntimeException("Duplicated Class Instantiation: simelectricity.essential.Essential");

    	ClientConfigs.register();
    	proxy.registerModelLoaders();
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public final static class ModEventBusHandler {
    	@SubscribeEvent
    	public static void newRegistry(RegistryEvent.NewRegistry event) {
        	SEEAPI.coverPanelRegistry = CoverPanelRegistry.INSTANCE;
    	}

    	@SubscribeEvent
		public static void registerBlocks(final RegistryEvent.Register<Block> event) {
    		BlockRegistry.initBlocks();
        	BlockRegistry.registerBlocks(event.getRegistry());
    	}

    	@SubscribeEvent
		public static void registerItems(final RegistryEvent.Register<Item> event) {
    		ItemRegistry.initItems();
        	BlockRegistry.registerBlockItems(event.getRegistry());
            ItemRegistry.registerItems(event.getRegistry());
    	}

    	@SubscribeEvent
    	public static void onTileEntityTypeRegistration(final RegistryEvent.Register<BlockEntityType<?>> event) {
    		BlockEntityRegistry.registerAll(event.getRegistry());
    	}

    	@SubscribeEvent
    	public static void registerContainers(final RegistryEvent.Register<MenuType<?>> event) {
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

    		new SECoverPanelFactory();
    	}
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public final static class ForgeEventBusHandler {
		@SubscribeEvent
		public static void onChunkWatchEvent(ChunkWatchEvent.Watch event) {
			LevelChunk chunk = event.getPlayer().level.getChunk(event.getPos().x, event.getPos().z);

			for (Object tileEntity : chunk.getBlockEntities().values()) {
				if (tileEntity instanceof ISEChunkWatchSensitiveTile)
					((ISEChunkWatchSensitiveTile) tileEntity).onRenderingUpdateRequested();
			}
		}
    }

    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> beTypeOf(Class<T> teCls) {
    	return teCls == null ? () -> null : Lazy.of(() -> BlockEntityHelper.getBEType(Essential.MODID, teCls));
    }
}
