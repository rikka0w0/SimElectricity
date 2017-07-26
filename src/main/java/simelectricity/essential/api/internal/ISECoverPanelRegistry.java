package simelectricity.essential.api.internal;

import simelectricity.essential.api.ISECoverPanel;
import simelectricity.essential.api.ISECoverPanelFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface ISECoverPanelRegistry {
	void registerCoverPanelFactory(ISECoverPanelFactory factory);
	ISECoverPanel fromItemStack(ItemStack itemStack);
	ISECoverPanel fromNBT(NBTTagCompound nbt);
	ItemStack toItemStack(ISECoverPanel coverPanel);
}
