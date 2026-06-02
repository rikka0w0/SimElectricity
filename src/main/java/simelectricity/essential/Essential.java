package simelectricity.essential;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import rikka.librikka.blockentity.BlockEntityHelper;
import rikka.librikka.network.MessageContainerSyncBase;
import simelectricity.essential.api.ISEChunkWatchSensitiveBlockEntity;
import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.client.ClientConfigs;
import simelectricity.essential.coverpanel.CoverPanelRegistry;
import simelectricity.essential.coverpanel.SECoverPanelFactory;
import simelectricity.essential.utils.network.MessageContainerSync;

import java.util.function.Supplier;

@Mod(Essential.MODID)
public class Essential {
    public static final String MODID = "sime_essential";

    public static Essential instance;

    public Essential(net.neoforged.bus.api.IEventBus modEventBus) {
    	if (instance == null)
            instance = this;
    	else
            throw new RuntimeException("Duplicated Class Instantiation: simelectricity.essential.Essential");

    	ClientConfigs.register();
        SEEAPI.coverPanelRegistry = CoverPanelRegistry.INSTANCE;

        BlockRegistry.init(modEventBus);
        ItemRegistry.init(modEventBus);
        BlockEntityRegistry.init(modEventBus);
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
    public final static class ModEventBusHandler {

        @SubscribeEvent
        public static void registerNetworking(final RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar(Essential.MODID).versioned("1");
            registrar.playBidirectional(
                MessageContainerSync.TYPE,
                MessageContainerSync.STREAM_CODEC,
                (msg, ctx) -> msg.handle(ctx)
            );
        }

    	@SubscribeEvent
    	public static void onCommonSetup(FMLCommonSetupEvent event) {
    		new SECoverPanelFactory();
    	}

        @SubscribeEvent
        public static void registerCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
            event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK,
                rikka.librikka.blockentity.BlockEntityHelper.getBEType(Essential.MODID, simelectricity.essential.machines.blockentity.BlockEntitySE2RF.class),
                (tile, side) -> {
                    if (side == tile.getFacing()) {
                        return tile.rfBufferHandler;
                    }
                    return null;
                }
            );
            event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK,
                rikka.librikka.blockentity.BlockEntityHelper.getBEType(Essential.MODID, simelectricity.essential.machines.blockentity.BlockEntityRF2SE.class),
                (tile, side) -> {
                    if (side == tile.getFacing()) {
                        return tile.rfBufferHandler;
                    }
                    return null;
                }
            );
            event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                rikka.librikka.blockentity.BlockEntityHelper.getBEType(Essential.MODID, simelectricity.essential.machines.blockentity.BlockEntityElectricFurnace.class),
                (tile, side) -> tile.itemStackHandler
            );
        }
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME)
    public final static class ForgeEventBusHandler {
		@SubscribeEvent
		public static void onChunkWatchEvent(ChunkWatchEvent.Watch event) {
			LevelChunk chunk = event.getLevel().getChunk(event.getPos().x, event.getPos().z);

			for (Object tileEntity : chunk.getBlockEntities().values()) {
				if (tileEntity instanceof ISEChunkWatchSensitiveBlockEntity)
					((ISEChunkWatchSensitiveBlockEntity) tileEntity).onRenderingUpdateRequested();
			}
		}
    }

    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> beTypeOf(Class<T> teCls) {
        if (teCls == null) return () -> null;
        return new Supplier<BlockEntityType<T>>() {
            private BlockEntityType<T> value;
            @Override
            public BlockEntityType<T> get() {
                if (value == null) {
                    value = BlockEntityHelper.getBEType(Essential.MODID, teCls);
                }
                return value;
            }
        };
    }
}
