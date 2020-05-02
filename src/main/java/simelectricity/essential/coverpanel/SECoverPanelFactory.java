package simelectricity.essential.coverpanel;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import simelectricity.essential.api.ISECoverPanelFactory;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.items.ItemMisc;

public class SECoverPanelFactory implements ISECoverPanelFactory {

    @Override
    public boolean acceptItemStack(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemMisc;
    }

    @Override
    public ISECoverPanel fromItemStack(ItemStack itemStack) {
    	Item item = itemStack.getItem();
        if (item instanceof ItemMisc) {
        	return ((ItemMisc) item).itemType.constructor.get();
        }

        return null;
    }

    @Override
    public boolean acceptNBT(CompoundNBT nbt) {
        String coverPanelType = nbt.getString("coverPanelType");
        return coverPanelType.equals("LedPanel") ||
                coverPanelType.equals("VoltageSensorPanel");
    }

    @Override
    public ISECoverPanel fromNBT(CompoundNBT nbt) {
        String coverPanelType = nbt.getString("coverPanelType");

        if (coverPanelType.equals("LedPanel"))
            return new LedPanel();

        if (coverPanelType.equals("VoltageSensorPanel"))
            return new VoltageSensorPanel(nbt);

        return null;
    }
}
