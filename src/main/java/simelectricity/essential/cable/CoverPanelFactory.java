package simelectricity.essential.cable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import simelectricity.essential.api.ISECoverPanel;
import simelectricity.essential.api.internal.ISECoverPanelFactory;
import simelectricity.essential.extensions.ExtensionBuildCraft;

public class CoverPanelFactory implements ISECoverPanelFactory{	
	@Override
	public ISECoverPanel fromItemStack(ItemStack itemStack) {
		ISECoverPanel result = null;
		
		//First check if the itemStack is a buildcraft Facade
		if (ExtensionBuildCraft.bcCoverPanelFactory != null){
			ISECoverPanel bcCoverPanel = 
					ExtensionBuildCraft.bcCoverPanelFactory.fromItemStack(itemStack);
			
			if (bcCoverPanel != null)
				return bcCoverPanel;
		}
		
		// TODO Attempt to create SECoverPanel
		return null;
	}

	@Override
	public ISECoverPanel fromNBT(NBTTagCompound nbt) {
		boolean isBCFacade = nbt.getBoolean("isBCFacade");
		boolean isHollow = nbt.getBoolean("isHollow");
		byte meta = nbt.getByte("meta");
		int blockID = nbt.getInteger("blockID");

		if (blockID == 0)
			return null;
		
		Block block = Block.getBlockById(blockID);
		
        return new CoverPanel(
        		isBCFacade,
        		isHollow,
        		meta,
        		block
        		);
	}
}
