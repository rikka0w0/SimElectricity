package simelectricity.essential.api.coverpanel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

public interface ISEGuiCoverPanel extends ISECoverPanel{
	Container getServerContainer(TileEntity te);
	
	@SideOnly(Side.CLIENT)
	GuiContainer getClientGuiContainer(TileEntity te);
}
