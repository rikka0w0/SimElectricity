package simelectricity.essential.cable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import simelectricity.essential.ItemRegistry;
import simelectricity.essential.api.ISECoverPanel;
import simelectricity.essential.api.ISECoverPanelFactory;
import simelectricity.essential.items.ItemMisc;

public class SECoverPanelFactory implements ISECoverPanelFactory{

	@Override
	public boolean acceptItemStack(ItemStack itemStack) {
		if (itemStack.getItem() instanceof ItemMisc){
			switch (itemStack.getItemDamage()){
			case 0:	//LED Panel
				return true;
			}
		}
		
		return false;
	}

	@Override
	public ISECoverPanel fromItemStack(ItemStack itemStack) {
		if (itemStack.getItem() instanceof ItemMisc){
			switch (itemStack.getItemDamage()){
			case 0:	//LED Panel
				return new LedPanel();
			}
		}
		
		return null;
	}

	@Override
	public boolean acceptNBT(NBTTagCompound nbt) {
		String coverPanelType = nbt.getString("coverPanelType");
		return coverPanelType.equals("LedPanel");
	}

	@Override
	public ISECoverPanel fromNBT(NBTTagCompound nbt) {
		String coverPanelType = nbt.getString("coverPanelType");
		
		if (coverPanelType.equals("LedPanel"))
			return new LedPanel();
		
		return null;
	}

}
