package simelectricity.essential;

import simelectricity.essential.items.ItemHighVoltageCable;
import simelectricity.essential.items.ItemMisc;
import simelectricity.essential.items.ItemVitaTea;

public class ItemRegistry {	
	public static ItemHighVoltageCable itemHVCable;
	public static ItemVitaTea itemVitaTea;
	public static ItemMisc itemMisc;
	
	public static void registerItems(){
		itemVitaTea = new ItemVitaTea();
		itemHVCable = new ItemHighVoltageCable();
		itemMisc = new ItemMisc();
	}
}
