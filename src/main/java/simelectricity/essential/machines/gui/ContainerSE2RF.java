package simelectricity.essential.machines.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.container.IContainerWithGui;
import simelectricity.essential.common.ContainerNoInvAutoSync;
import simelectricity.essential.machines.tile.TileSE2RF;

public class ContainerSE2RF extends ContainerNoInvAutoSync<TileSE2RF> implements IContainerWithGui {
	@ContainerSynchronizer.SyncField
	public double bufferedEnergy;

    @ContainerSynchronizer.SyncField
    public double voltage;
    @ContainerSynchronizer.SyncField
    public double actualInputPower;


    public ContainerSE2RF(TileEntity tileEntity) {
		super(tileEntity);
	}

    @Override
    @SideOnly(Side.CLIENT)
	public GuiScreen createGui() {
		return new GuiSE2RF(this);
	}
}
