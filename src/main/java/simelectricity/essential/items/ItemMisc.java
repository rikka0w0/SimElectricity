package simelectricity.essential.items;

import java.util.function.Supplier;

import net.minecraft.item.Item;
import rikka.librikka.IMetaProvider;
import rikka.librikka.IMetaBase;
import rikka.librikka.item.ItemBase;
import simelectricity.api.SEAPI;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.coverpanel.LedPanel;
import simelectricity.essential.coverpanel.VoltageSensorPanel;
import simelectricity.essential.coverpanel.FacadePanel;

/**
 * The ItemMisc creates very simple and basic items
 * e.g. raw materials and things cannot be "used" by players
 * @author Rikka0w0
 */
public final class ItemMisc extends ItemBase implements IMetaProvider<IMetaBase> {
    public static enum ItemType implements IMetaBase {
    	ledpanel(LedPanel::new),
    	voltagesensor(VoltageSensorPanel::new),
    	facade(FacadePanel.FacadeNormal::new),
    	facade_hollow(FacadePanel.FacadeHollow::new);
    	
    	public final Supplier<ISECoverPanel> constructor;
    	ItemType(Supplier<ISECoverPanel> constructor) {
    		this.constructor = constructor;
    	}
    }
    
    public final ItemType itemType;
    private ItemMisc(ItemType itemType) {
        super("item_" + itemType.name(), (new Item.Properties())
        		.group(SEAPI.SETab));
        this.itemType = itemType;
    }
    
    @Override
	public IMetaBase meta() {
		return itemType;
	}
    
    public static ItemMisc[] create() {
    	ItemMisc[] ret = new ItemMisc[ItemType.values().length];
    	for (ItemType meta: ItemType.values()) {
    		ret[meta.ordinal()] = new ItemMisc(meta);
    	}
    	return ret;
    }
}
