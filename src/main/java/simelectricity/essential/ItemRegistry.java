package simelectricity.essential;

import simelectricity.essential.items.ItemHighVoltageCable;
import simelectricity.essential.items.ItemMisc;
import simelectricity.essential.items.ItemTools;
import simelectricity.essential.items.ItemVitaTea;

public class ItemRegistry {
    public static ItemHighVoltageCable itemHVCable;
    public static ItemVitaTea itemVitaTea;
    public static ItemMisc itemMisc;
    public static ItemTools itemTools;

    public static void registerItems() {
        ItemRegistry.itemVitaTea = new ItemVitaTea();
        ItemRegistry.itemHVCable = new ItemHighVoltageCable();
        ItemRegistry.itemMisc = new ItemMisc();
        ItemRegistry.itemTools = new ItemTools();
    }
}
