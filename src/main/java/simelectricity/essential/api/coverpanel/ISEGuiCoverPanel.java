package simelectricity.essential.api.coverpanel;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISEGuiCoverPanel extends ISECoverPanel {
    Container getServerContainer(TileEntity te);

    @SideOnly(Side.CLIENT)
    GuiContainer getClientGuiContainer(TileEntity te);
}
