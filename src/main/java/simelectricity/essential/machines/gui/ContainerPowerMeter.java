package simelectricity.essential.machines.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.container.IContainerWithGui;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.ContainerNoInventoryTwoPort;
import simelectricity.essential.machines.tile.TilePowerMeter;
import simelectricity.essential.utils.network.ISEButtonEventHandler;

public class ContainerPowerMeter extends ContainerNoInventoryTwoPort<TilePowerMeter> implements ISEButtonEventHandler, IContainerWithGui {
    @ContainerSynchronizer.SyncField
    public volatile boolean isOn;
    @ContainerSynchronizer.SyncField
    public volatile double current;
    @ContainerSynchronizer.SyncField
    public volatile double voltage;
    @ContainerSynchronizer.SyncField
    public volatile double bufferedEnergy;
    @ContainerSynchronizer.SyncField
    public volatile EnumFacing inputSide, outputSide;


    public ContainerPowerMeter(TileEntity tileEntity) {
        super(tileEntity);
    }

    @Override
    public void onButtonPressed(int buttonID, boolean isCtrlPressed) {
        switch (buttonID) {
            case 0:
                host.isOn = !host.isOn;
                SEAPI.energyNetAgent.updateTileParameter(host);
                break;
            case 1:
                host.bufferedEnergy = 0;
                break;
            default:
                break;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen createGui() {
        return new GuiPowerMeter(this);
    }
}
