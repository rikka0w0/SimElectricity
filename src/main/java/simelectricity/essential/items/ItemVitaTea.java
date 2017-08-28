package simelectricity.essential.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.item.ISESimpleTextureItem;
import rikka.librikka.item.SEItem;
import simelectricity.api.SEAPI;

public class ItemVitaTea extends SEItem implements ISESimpleTextureItem {
    public ItemVitaTea() {
        super("cell_vita", false);

    }

    @Override
    public void beforeRegister() {
        setCreativeTab(SEAPI.SETab);
    }

    @Override
    public String getIconName(int damage) {
        return "cell_vita";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
