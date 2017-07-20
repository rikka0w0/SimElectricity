package simelectricity.essential.api;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface ISECoverPanel {
	public static final double thickness = 0.05;	//Constant
	
	/**
	 * @return false: prevent other cable/machine from connecting to the side with this cover panel
	 */
	boolean isHollow();	
	
	/**
	 * @return a safe copy of the ItemStack, stack size always 1
	 */
	ItemStack getCoverPanelItem();

	
	/**
	 * Save the cover panel to a NBT object
	 * @param nbt
	 */
	void toNBT(NBTTagCompound nbt);
	
	@SideOnly(Side.CLIENT)
	ISECoverPanelRender getCoverPanelRender();
}
