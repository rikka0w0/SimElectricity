package simelectricity.essential.machines.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.Direction;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.gui.AutoGuiHandler;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.ContainerNoInventoryTwoPort;
import simelectricity.essential.machines.tile.TileSwitch;
import simelectricity.essential.utils.network.ISEButtonEventHandler;

@AutoGuiHandler.Marker(GuiSwitch.class)
public class ContainerSwitch extends ContainerNoInventoryTwoPort<TileSwitch> implements ISEButtonEventHandler {
	@ContainerSynchronizer.SyncField
	public volatile double resistance;
	@ContainerSynchronizer.SyncField
    public volatile boolean isOn;
    @ContainerSynchronizer.SyncField
    public volatile double maxCurrent;
    @ContainerSynchronizer.SyncField
    public volatile double current;
    @ContainerSynchronizer.SyncField
    public volatile Direction inputSide, outputSide;


    // Server side
    public ContainerSwitch(TileSwitch tileEntity, int windowId) {
    	super(tileEntity, windowId);
    }

    // Client side
    public ContainerSwitch(int windowId, Inventory inv) {
    	this(null, windowId);
    }

    @Override
    public void onButtonPressed(int buttonID, boolean isCtrlPressed) {
        double resistance = host.resistance;
        double maxCurrent = host.maxCurrent;
        boolean isOn = this.isOn;

        boolean isOnChanged = false;

        switch (buttonID) {
            case 0:
                maxCurrent -= 100;
                break;
            case 1:
                maxCurrent -= 10;
                break;
            case 2:
                if (isCtrlPressed)
                    maxCurrent -= 0.1;
                else
                    maxCurrent -= 1;
                break;
            case 3:
                if (isCtrlPressed)
                    maxCurrent += 0.1;
                else
                    maxCurrent += 1;
                break;
            case 4:
                maxCurrent += 10;
                break;
            case 5:
                maxCurrent += 100;
                break;


            case 6:
                resistance -= 1;
                break;
            case 7:
                resistance -= 0.1;
                break;
            case 8:
                if (isCtrlPressed)
                    resistance -= 0.001;
                else
                    resistance -= 0.01;
                break;
            case 9:
                if (isCtrlPressed)
                    resistance += 0.001;
                else
                    resistance += 0.01;
                break;
            case 10:
                resistance += 0.1;
                break;
            case 11:
                resistance += 1;
                break;


            case 12:
                isOnChanged = true;
                isOn = !isOn;
                break;
            default:
        }

        if (resistance < 0.001)
            resistance = 0.001;
        if (resistance > 100)
            resistance = 100;

        if (maxCurrent < 0.1)
            maxCurrent = 0.1;
        if (maxCurrent > 1000)
            maxCurrent = 1000;

        host.resistance = resistance;
        host.maxCurrent = maxCurrent;

        if (isOnChanged) {
            host.setSwitchStatus(isOn);
        } else {
            SEAPI.energyNetAgent.updateTileParameter(host);
        }
    }
}
