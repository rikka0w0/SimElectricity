package simelectricity;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import simelectricity.api.SEAPI;
import simelectricity.common.CommandSimE;
import simelectricity.common.ConfigManager;
import simelectricity.common.ItemSEMgrTool;
import simelectricity.common.SELogger;
import simelectricity.energynet.EnergyNetAgent;
import simelectricity.essential.BlockRegistry;
import simelectricity.essential.ItemRegistry;

@Mod(SimElectricity.MODID)
public class SimElectricity {
    public static final String MODID = "simelectricity";
    public static final String version = "1.0.0";

    public static SimElectricity instance = null;

    public SimElectricity() {
        if (instance == null)
            instance = this;
        else
            throw new RuntimeException("Duplicated Class Instantiation: SimElectricity");
        
        ConfigManager.register();

        //Initialize SEAPI
        SEAPI.isSELoaded = true;
        SEAPI.energyNetAgent = new EnergyNetAgent();
    }
    
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
    public final static class ModEventBusHandler {
        @SubscribeEvent
        public static void onRegister(RegisterEvent event) {
            if (event.getRegistryKey().equals(Registries.ITEM)) {
                SEAPI.managementToolItem = new ItemSEMgrTool();
                event.register(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MODID, ItemSEMgrTool.name), () -> SEAPI.managementToolItem);
            }

            if (event.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
                event.register(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(MODID, "creative_tab"), () -> {
                    SEAPI.SETab = CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.simelectricity"))
                            .icon(() -> new ItemStack(SEAPI.managementToolItem))
                            .displayItems((params, output) -> {
                                output.accept(SEAPI.managementToolItem);
                                BlockRegistry.addItemsToCreativeTab(output);
                                ItemRegistry.addItemsToCreativeTab(output);
                            })
                            .build();
                    return SEAPI.SETab;
                });
            }
        }
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME)
    public final static class ForgeEventBusHandler {
        @SubscribeEvent
        public static void onServerStarting(RegisterCommandsEvent e) {
            CommandSimE.register(e.getDispatcher());
            SELogger.logInfo(SELogger.loader, "Server command registered");
        }
    }
}
