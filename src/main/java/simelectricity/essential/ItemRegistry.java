package simelectricity.essential;

import net.minecraftforge.registries.IForgeRegistry;
import simelectricity.essential.items.ItemHighVoltageCable;
import simelectricity.essential.items.ItemMisc;
import simelectricity.essential.items.ItemTools;
import simelectricity.essential.items.ItemFutaTea;

public class ItemRegistry {
    public static ItemHighVoltageCable[] itemHVCable;
    public static ItemFutaTea itemFutaTea;
    public static ItemMisc[] itemMisc;
    public static ItemTools[] itemTools;

    public static void initItems() {
        itemHVCable = ItemHighVoltageCable.create();
        itemFutaTea = new ItemFutaTea();
        itemMisc = ItemMisc.create();
        itemTools = ItemTools.create();
    }
    
    public static void registerItems(IForgeRegistry registry) {
    	registry.registerAll(itemHVCable);
    	registry.registerAll(itemFutaTea);
    	registry.registerAll(itemMisc);
    	registry.registerAll(itemTools);
    }
}
