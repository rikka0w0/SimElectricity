package simelectricity.essential.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.Essential;

public abstract class SEItem extends Item {
    /**
     * @param name        Naming rules: lower case English letters and numbers only, words are separated by '_', e.g. "cooked_beef"
     * @param hasSubItems
     */
    public SEItem(String name, boolean hasSubItems) {
        setUnlocalizedName(name);    //UnlocalizedName = "item." + name
        setRegistryName(name);
        setHasSubtypes(hasSubItems);

        if (hasSubItems)
            setMaxDamage(0);    //The item can not be damaged

        beforeRegister();

        GameRegistry.register(this);
    }

    @Override
    public final String getUnlocalizedName(ItemStack itemstack) {
        if (getHasSubtypes()) {
            return getUnlocalizedName() + "." + this.getSubItemUnlocalizedNames()[itemstack.getItemDamage()];
        } else {
            return getUnlocalizedName();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public final void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (getHasSubtypes()) {
            for (int ix = 0; ix < this.getSubItemUnlocalizedNames().length; ix++)
                subItems.add(new ItemStack(this, 1, ix));
        } else {
            subItems.add(new ItemStack(itemIn));
        }
    }

    @Override
    public final String getUnlocalizedNameInefficiently(ItemStack stack) {
        String prevName = super.getUnlocalizedNameInefficiently(stack);
        return "item." + Essential.modID + ":" + prevName.substring(5);
    }

    public abstract void beforeRegister();

    /**
     * Only use for subItems
     *
     * @return an array of unlocalized names
     */
    public String[] getSubItemUnlocalizedNames() {
        return null;
    }
}
