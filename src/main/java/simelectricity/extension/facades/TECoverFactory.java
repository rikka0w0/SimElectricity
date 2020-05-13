package simelectricity.extension.facades;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import simelectricity.essential.api.ISECoverPanelFactory;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

public class TECoverFactory implements ISECoverPanelFactory{
    @Override
    public ISECoverPanel from(ItemStack itemStack) {
    	if (!itemStack.getItem().getTranslationKey().startsWith("item.thermaldynamics.cover"))
    		return null;
    	
        CompoundNBT nbt = itemStack.getTag();
        if (nbt == null)
        	return null;

    	// TODO: We don't know TE's NBT format in 1.15.2 yet
//        if (nbt.hasKey("Meta", 1) && nbt.hasKey("Block", 8)) {
//            String blockName = nbt.getString("Block");
//            Block block = Block.getBlockFromName(blockName);
//            int meta = nbt.getByte("Meta");
//
//            return new TEFacadePanel(block.getStateFromMeta(meta), itemStack);
//        }
        return null;
    }

    @Override
    public ISECoverPanel from(CompoundNBT nbt, Class<? extends ISECoverPanel> panelCls, String coverPanelName) {
        return new TEFacadePanel(nbt);
    }

	@Override
	public String getName() {
		return "TECoverFactory";
	}
}
