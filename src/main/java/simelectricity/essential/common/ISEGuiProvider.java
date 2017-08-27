package simelectricity.essential.common;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISEGuiProvider {
    Container getServerContainer(EnumFacing side);

    @SideOnly(Side.CLIENT)
    GuiContainer getClientGuiContainer(EnumFacing side);
}
