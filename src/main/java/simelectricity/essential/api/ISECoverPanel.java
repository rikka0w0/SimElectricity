package simelectricity.essential.api;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface ISECoverPanel {
	boolean isBuildCraftFacade();
	boolean isHollow();
	int getBlockMeta();
	Block getBlock();
	ItemStack getCoverPanelItem();
	void toNBT(NBTTagCompound nbt);
}
