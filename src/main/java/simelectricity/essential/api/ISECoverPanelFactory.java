package simelectricity.essential.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

public interface ISECoverPanelFactory {
	/**
	 * Create ISECoverPanel instance from itemstack
	 * @param itemStack
	 * @return the ISECoverPanel instance, null if itemstack is rejected
	 */
	@Nullable
    ISECoverPanel from(@Nonnull ItemStack itemStack);

	/**
	 * Create ISECoverPanel instance from panelCls, 
	 * additional information such as the name and the other NBT tags are available
	 * @param nbt the NBT tag that contains all necessary information about the cover panel, see {@link ISECoverPanel#toNBT}
	 * @param panelCls the class of the cover panel, from registry look-up
	 * @param coverPanelName the registry name of the cover panel instance
	 * @return the ISECoverPanel instance
	 */
    ISECoverPanel from(CompoundNBT nbt, Class<? extends ISECoverPanel> panelCls, String coverPanelName);
	
	String getName();
}
