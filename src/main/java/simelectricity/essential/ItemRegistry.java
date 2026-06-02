package simelectricity.essential;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import simelectricity.essential.items.ItemHighVoltageCable;
import simelectricity.essential.items.ItemPanel;
import simelectricity.essential.items.ItemTools;
import simelectricity.essential.items.ItemFutaTea;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Essential.MODID);

    public static ItemHighVoltageCable[] itemHVCable;
    public static ItemFutaTea itemFutaTea;
    public static ItemPanel[] itemMisc;
    public static ItemTools[] itemTools;

    public static void init(IEventBus modEventBus) {
        itemHVCable = ItemHighVoltageCable.create();
        itemFutaTea = new ItemFutaTea();
        itemMisc = ItemPanel.create();
        itemTools = ItemTools.create();

        registerItems(itemHVCable);
        ITEMS.register(itemFutaTea.registryName, () -> itemFutaTea);
        registerItems(itemMisc);
        registerItems(itemTools);

        ITEMS.register(modEventBus);
    }

    private static void registerItems(rikka.librikka.item.ItemBase... items) {
        for (rikka.librikka.item.ItemBase item : items) {
            ITEMS.register(item.registryName, () -> item);
        }
    }

    public static void addItemsToCreativeTab(CreativeModeTab.Output output) {
        for (Item item : itemHVCable) {
            output.accept(item);
        }
        output.accept(itemFutaTea);
        for (Item item : itemMisc) {
            output.accept(item);
        }
        for (Item item : itemTools) {
            output.accept(item);
        }
    }
}
