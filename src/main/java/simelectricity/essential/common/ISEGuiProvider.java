package simelectricity.essential.common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraftforge.common.util.ForgeDirection;

public interface ISEGuiProvider {
	Container getServerContainer(ForgeDirection side);
	
	@SideOnly(Side.CLIENT)
	GuiContainer getClientGuiContainer(ForgeDirection side);
}
