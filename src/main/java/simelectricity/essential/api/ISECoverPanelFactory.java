package simelectricity.essential.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface ISECoverPanelFactory{
	boolean acceptItemStack(ItemStack itemStack);
	ISECoverPanel fromItemStack(ItemStack itemStack);
	
	boolean acceptNBT(NBTTagCompound nbt);
	ISECoverPanel fromNBT(NBTTagCompound nbt);
	
	boolean acceptCoverPanel(ISECoverPanel coverPanel);
	/**
	 * @return a safe copy of the ItemStack, stack size always 1
	 */
	ItemStack getItemStack(ISECoverPanel coverPanel);
}
