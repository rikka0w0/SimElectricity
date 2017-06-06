package simelectricity.essential.api.internal;

import simelectricity.essential.api.ISECoverPanel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface ISECoverPanelFactory{
	ISECoverPanel fromItemStack(ItemStack itemStack);
	ISECoverPanel fromNBT(NBTTagCompound nbt);
}
