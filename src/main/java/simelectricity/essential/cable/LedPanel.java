package simelectricity.essential.cable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simelectricity.essential.ItemRegistry;
import simelectricity.essential.api.ISECoverPanel;
import simelectricity.essential.api.ISECoverPanelRender;
import simelectricity.essential.cable.render.RenderLedPanel;

public class LedPanel implements ISECoverPanel{
	@Override
	public boolean isHollow() {
		return false;
	}

	@Override
	public ItemStack getCoverPanelItem() {
		return new ItemStack(ItemRegistry.itemMisc, 1, 0);
	}

	@Override
	public void toNBT(NBTTagCompound nbt) {
		nbt.setString("coverPanelType", "LedPanel");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ISECoverPanelRender getCoverPanelRender() {
		return RenderLedPanel.instance;
	}

}
