package simelectricity.essential.machines.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.container.IContainerWithGui;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.ContainerNoInvAutoSync;
import simelectricity.essential.machines.tile.TileSE2RF;
import simelectricity.essential.utils.network.ISEButtonEventHandler;

public class ContainerSE2RF extends ContainerNoInvAutoSync<TileSE2RF> implements ISEButtonEventHandler, IContainerWithGui {
	@ContainerSynchronizer.SyncField
	public double bufferedEnergy;

    @ContainerSynchronizer.SyncField
    public double voltage;
    @ContainerSynchronizer.SyncField
    public double actualInputPower;
    @ContainerSynchronizer.SyncField
    public int rfDemandRateDisplay;
    @ContainerSynchronizer.SyncField
    public int rfOutputRateDisplay;

    @ContainerSynchronizer.SyncField
    public double ratedOutputPower;

    public ContainerSE2RF(TileEntity tileEntity) {
		super(tileEntity);
	}

    @Override
    @SideOnly(Side.CLIENT)
	public GuiScreen createGui() {
		return new GuiSE2RF(this);
	}

    @Override
    public void onButtonPressed(int buttonID, boolean isCtrlPressed) {
        double ratedOutputPower = host.ratedOutputPower;

        switch (buttonID) {
            case 0:
                ratedOutputPower -= 100;
                break;
            case 1:
                ratedOutputPower -= 10;
                break;
            case 2:
                ratedOutputPower -= 1;
            case 3:
                ratedOutputPower += 1;
                break;
            case 4:
                ratedOutputPower += 10;
                break;
            case 5:
                ratedOutputPower += 100;
                break;
        }

        if (ratedOutputPower < 50)
            ratedOutputPower = 50;
        if (ratedOutputPower > TileSE2RF.bufferCapacity / 2)
            ratedOutputPower = TileSE2RF.bufferCapacity / 2;

        host.ratedOutputPower = ratedOutputPower;

        SEAPI.energyNetAgent.updateTileParameter(this.host);
    }
}
