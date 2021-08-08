package simelectricity.essential.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import rikka.librikka.item.ItemBase;
import simelectricity.api.SEAPI;

public class ItemFutaTea extends ItemBase {
    public ItemFutaTea() {
        super("futa_lemon_tea", (new Item.Properties())
        		.tab(SEAPI.SETab));
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
