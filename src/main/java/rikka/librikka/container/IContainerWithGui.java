package rikka.librikka.container;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The container implements this interface to provide a corresponding Gui on CLIENT side
 * @author Rikka0_0
 */
public interface IContainerWithGui {
	@SideOnly(Side.CLIENT)
	GuiScreen createGui();
}
