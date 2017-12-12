package simelectricity.essential.machines.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.container.IContainerWithGui;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.ContainerNoInvAutoSync;
import simelectricity.essential.machines.tile.TileAdjustableResistor;
import simelectricity.essential.utils.network.ISEButtonEventHandler;

public class ContainerAdjustableResistor extends ContainerNoInvAutoSync<TileAdjustableResistor> implements ISEButtonEventHandler, IContainerWithGui {
    @ContainerSynchronizer.SyncField
    public double resistance;
    @ContainerSynchronizer.SyncField
    public double voltage;
    @ContainerSynchronizer.SyncField
    public double current;
    @ContainerSynchronizer.SyncField
    public double powerLevel;
    @ContainerSynchronizer.SyncField
    public double bufferedEnergy;

    public ContainerAdjustableResistor(TileEntity tileEntity) {
        super(tileEntity);
    }

    @Override
    public void onButtonPressed(int buttonID, boolean isCtrlPressed) {
        double resistance = host.resistance;

        switch (buttonID) {
            case 0:
                resistance -= 100;
                break;
            case 1:
                resistance -= 10;
                break;
            case 2:
                if (isCtrlPressed)
                    resistance -= 0.1;
                else
                    resistance -= 1;
                break;
            case 3:
                if (isCtrlPressed)
                    resistance += 0.1;
                else
                    resistance += 1;
                break;
            case 4:
                resistance += 10;
                break;
            case 5:
                resistance += 100;
                break;

            case 6:
                host.bufferedEnergy = 0;
                return;
        }

        if (resistance < 0.1)
            resistance = 0.1;
        if (resistance > 10000)
            resistance = 10000;

        host.resistance = resistance;

        SEAPI.energyNetAgent.updateTileParameter(this.host);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen createGui() {
        return new GuiAdjustableResistor(this);
    }
}
