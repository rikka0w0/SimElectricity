package simElectricity.API.Common.Items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import simElectricity.API.Util;

public class ItemSE extends Item {
    public ItemSE() {
        super();
        if (registerInCreativeTab())
            setCreativeTab(Util.SETab);
    }

    @Override
    public Item setUnlocalizedName(String name) {
        if (shouldRegister())
            GameRegistry.registerItem(this, name);
        return super.setUnlocalizedName(name);
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack itemStack) {
        return super.getUnlocalizedNameInefficiently(itemStack).replaceAll("item.", "item.sime:");
    }

    public boolean registerInCreativeTab() {
        return true;
    }

    public boolean shouldRegister() {
        return true;
    }
}
