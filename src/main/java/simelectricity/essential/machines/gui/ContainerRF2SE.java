package simelectricity.essential.machines.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.container.IContainerWithGui;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.ContainerNoInvAutoSync;
import simelectricity.essential.machines.tile.TileRF2SE;
import simelectricity.essential.utils.network.ISEButtonEventHandler;

public class ContainerRF2SE extends ContainerNoInvAutoSync<TileRF2SE> implements ISEButtonEventHandler, IContainerWithGui {
    @ContainerSynchronizer.SyncField
    public double ratedOutputPower;
    @ContainerSynchronizer.SyncField
    public double voltage;
    @ContainerSynchronizer.SyncField
    public double actualOutputPower;
    @ContainerSynchronizer.SyncField
    public int bufferedEnergy;
    @ContainerSynchronizer.SyncField
    public int rfInputRateDisplay;

    public ContainerRF2SE(TileEntity tileEntity) {
        super(tileEntity);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen createGui() {
        return new GuiRF2SE(this);
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

        if (ratedOutputPower < 10)
            ratedOutputPower = 10;
        if (ratedOutputPower > TileRF2SE.bufferCapacity / 2)
            ratedOutputPower = TileRF2SE.bufferCapacity / 2;

        host.ratedOutputPower = ratedOutputPower;

        // SEAPI.energyNetAgent.updateTileParameter(this.host);
    }
}
