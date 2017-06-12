package simelectricity.essential;

import simelectricity.essential.common.SEItem;
import simelectricity.essential.items.ItemVitaTea;

public class ItemRegistry {	
	public static SEItem itemVitaTea;
	
	public static void registerItems(){
		itemVitaTea = new ItemVitaTea();
	}
}
