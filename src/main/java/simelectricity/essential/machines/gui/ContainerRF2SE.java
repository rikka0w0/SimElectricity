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
    public double internalVoltage;
    @ContainerSynchronizer.SyncField
    public double resistance;
    @ContainerSynchronizer.SyncField
    public double voltage;
    @ContainerSynchronizer.SyncField
    public double current;

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
        double resistance = host.resistance;
        double internalVoltage = host.internalVoltage;

        switch (buttonID) {
            case 0:
                resistance -= 1;
                break;
            case 1:
                resistance -= 0.1;
                break;
            case 2:
                resistance -= 0.01;
                break;
            case 3:
                resistance += 0.01;
                break;
            case 4:
                resistance += 0.1;
                break;
            case 5:
                resistance += 1;
                break;

            case 7:
                internalVoltage -= 10;
                break;
            case 8:
                internalVoltage -= 1;
                break;
            case 9:
                internalVoltage += 1;
                break;
            case 10:
                internalVoltage += 10;
                break;
        }

        if (resistance < 0.01)
            resistance = 0.01;
        if (resistance > 10)
            resistance = 10;

        if (internalVoltage < 180)
            internalVoltage = 180;
        if (internalVoltage > 265)
            internalVoltage = 265;

        host.resistance = resistance;
        host.internalVoltage = internalVoltage;

        SEAPI.energyNetAgent.updateTileParameter(this.host);
    }
}
