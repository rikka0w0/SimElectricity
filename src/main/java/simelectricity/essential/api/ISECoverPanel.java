package simelectricity.essential.api;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;

public interface ISECoverPanel {
	boolean isBuildCraftFacade();
	boolean isHollow();
	int getBlockMeta();
	Block getBlock();
	void toNBT(NBTTagCompound nbt);
}
