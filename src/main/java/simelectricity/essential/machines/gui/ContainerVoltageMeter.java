package simelectricity.essential.machines.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.container.IContainerWithGui;
import simelectricity.essential.common.ContainerNoInvAutoSync;
import simelectricity.essential.machines.tile.TileVoltageMeter;

public class ContainerVoltageMeter extends ContainerNoInvAutoSync<TileVoltageMeter> implements IContainerWithGui {
	@ContainerSynchronizer.SyncField
    public double voltage;

    public ContainerVoltageMeter(TileEntity tileEntity) {
        super(tileEntity);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen createGui() {
    	return new GuiVoltageMeter(this);
    }
}
