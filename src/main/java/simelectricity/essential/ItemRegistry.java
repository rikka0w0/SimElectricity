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
        // Allocate arrays
        itemHVCable = new ItemHighVoltageCable[ItemHighVoltageCable.ItemType.values().length];
        itemMisc = new ItemPanel[ItemPanel.ItemType.values().length];
        itemTools = new ItemTools[ItemTools.ItemType.values().length];

        // Register ItemFutaTea
        ITEMS.register("futa_lemon_tea", () -> {
            itemFutaTea = new ItemFutaTea();
            return itemFutaTea;
        });

        // Register ItemHighVoltageCable
        for (ItemHighVoltageCable.ItemType meta : ItemHighVoltageCable.ItemType.values()) {
            final int index = meta.ordinal();
            final String name = "hvcable_" + meta.name();
            ITEMS.register(name, () -> {
                ItemHighVoltageCable item = ItemHighVoltageCable.createItem(meta);
                itemHVCable[index] = item;
                return item;
            });
        }

        // Register ItemPanel
        for (ItemPanel.ItemType meta : ItemPanel.ItemType.values()) {
            final int index = meta.ordinal();
            final String name = "item_" + meta.name();
            ITEMS.register(name, () -> {
                ItemPanel item = ItemPanel.createItem(meta);
                itemMisc[index] = item;
                return item;
            });
        }

        // Register ItemTools
        for (ItemTools.ItemType meta : ItemTools.ItemType.values()) {
            final int index = meta.ordinal();
            final String name = "tool_" + meta.name();
            ITEMS.register(name, () -> {
                ItemTools item = ItemTools.createItem(meta);
                itemTools[index] = item;
                return item;
            });
        }

        ITEMS.register(modEventBus);
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
