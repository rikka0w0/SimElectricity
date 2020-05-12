package simelectricity.essential.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

public interface ISECoverPanelFactory {
    ISECoverPanel from(ItemStack itemStack);

    ISECoverPanel from(CompoundNBT nbt, Class<? extends ISECoverPanel> panelCls, String coverPanelName);
	
	String getName();
}
