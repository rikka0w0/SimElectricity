package simelectricity.essential.cable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import simelectricity.essential.api.ISECoverPanelFactory;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.items.ItemMisc;

public class SECoverPanelFactory implements ISECoverPanelFactory{

	@Override
	public boolean acceptItemStack(ItemStack itemStack) {
		if (itemStack.getItem() instanceof ItemMisc){
			switch (itemStack.getItemDamage()){
			case 0:	//LED Panel
				return true;
			case 1:
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
			case 1:
				return new VoltageSensorPanel();
			}
		}
		
		return null;
	}

	@Override
	public boolean acceptNBT(NBTTagCompound nbt) {
		String coverPanelType = nbt.getString("coverPanelType");
		return 	coverPanelType.equals("LedPanel") ||
				coverPanelType.equals("VoltageSensorPanel");
	}

	@Override
	public ISECoverPanel fromNBT(NBTTagCompound nbt) {
		String coverPanelType = nbt.getString("coverPanelType");
		
		if (coverPanelType.equals("LedPanel"))
			return new LedPanel();
		
		if (coverPanelType.equals("VoltageSensorPanel"))
			return new VoltageSensorPanel(nbt);
		
		return null;
	}	
}
