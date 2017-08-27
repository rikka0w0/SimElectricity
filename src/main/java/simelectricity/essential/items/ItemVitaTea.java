package simelectricity.essential.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.SEAPI;
import simelectricity.essential.client.ISESimpleTextureItem;
import simelectricity.essential.common.SEItem;

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
