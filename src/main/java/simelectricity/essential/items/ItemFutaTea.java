package simelectricity.essential.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import rikka.librikka.item.ItemBase;
import simelectricity.api.SEAPI;

public class ItemFutaTea extends ItemBase {
    public ItemFutaTea() {
        super("futa_lemon_tea", (new Item.Properties())
        		.group(SEAPI.SETab));
    }
    
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
