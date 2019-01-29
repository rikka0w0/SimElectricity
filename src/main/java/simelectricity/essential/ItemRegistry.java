package simelectricity.essential;

import net.minecraftforge.registries.IForgeRegistry;
import simelectricity.essential.items.ItemHighVoltageCable;
import simelectricity.essential.items.ItemMisc;
import simelectricity.essential.items.ItemTools;
import simelectricity.essential.items.ItemVitaTea;

public class ItemRegistry {
    public static ItemHighVoltageCable itemHVCable;
    public static ItemVitaTea itemVitaTea;
    public static ItemMisc itemMisc;
    public static ItemTools itemTools;

    public static void initItems() {
        itemVitaTea = new ItemVitaTea();
        itemHVCable = new ItemHighVoltageCable();
        itemMisc = new ItemMisc();
        itemTools = new ItemTools();
    }
    
    public static void registerItems(IForgeRegistry registry) {
    	registry.registerAll(
    			itemVitaTea,
    			itemHVCable,
    			itemMisc,
    			itemTools
    			);
    }
}
