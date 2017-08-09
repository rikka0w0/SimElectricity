package simelectricity.essential.items;

import simelectricity.api.SEAPI;
import simelectricity.essential.client.ISESimpleTextureItem;
import simelectricity.essential.common.SEItem;

public class ItemMisc extends SEItem implements ISESimpleTextureItem{
	private final static String[] subNames = new String[]{"ledpanel", "voltagesensor"};
	
	public ItemMisc() {
		super("essential_item", true);
	}

	@Override
	public void beforeRegister() {
		this.setCreativeTab(SEAPI.SETab);
	}
	
	@Override
	public String[] getSubItemUnlocalizedNames(){
		return subNames;
	}

	@Override
	public String getIconName(int damage) {
		return "item_" + subNames[damage];
	}
}
