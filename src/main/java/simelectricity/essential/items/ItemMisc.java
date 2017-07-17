package simelectricity.essential.items;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.SEItem;

public class ItemMisc extends SEItem{
	public ItemMisc() {
		super("essential_item_misc", true);
	}

	@Override
	public void beforeRegister() {
		this.setCreativeTab(SEAPI.SETab);
	}
	
	@Override
	public String[] getSubItemUnlocalizedNames(){
		return new String[]{"ledpanel", "voltagepanel", "currentpanel"};
	}
}
