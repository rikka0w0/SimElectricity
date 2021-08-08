package simelectricity.essential.machines.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.Direction;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.gui.AutoGuiHandler;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.ContainerNoInventoryTwoPort;
import simelectricity.essential.machines.tile.TilePowerMeter;
import simelectricity.essential.utils.network.ISEButtonEventHandler;

@AutoGuiHandler.Marker(GuiPowerMeter.class)
public class ContainerPowerMeter extends ContainerNoInventoryTwoPort<TilePowerMeter> implements ISEButtonEventHandler {
    @ContainerSynchronizer.SyncField
    public volatile boolean isOn;
    @ContainerSynchronizer.SyncField
    public volatile double current;
    @ContainerSynchronizer.SyncField
    public volatile double voltage;
    @ContainerSynchronizer.SyncField
    public volatile double bufferedEnergy;
    @ContainerSynchronizer.SyncField
    public volatile Direction inputSide, outputSide;


    // Server side
    public ContainerPowerMeter(TilePowerMeter tileEntity, int windowId) {
    	super(tileEntity, windowId);
    }

    // Client side
    public ContainerPowerMeter(int windowId, Inventory inv) {
    	this(null, windowId);
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
}
