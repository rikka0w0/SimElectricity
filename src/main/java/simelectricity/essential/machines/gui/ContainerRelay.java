package simelectricity.essential.machines.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.gui.AutoGuiHandler;
import simelectricity.essential.common.ContainerNoInventoryTwoPort;
import simelectricity.essential.machines.tile.TileRelay;
import simelectricity.essential.utils.network.ISEButtonEventHandler;

@AutoGuiHandler.Marker(GuiRelay.class)
public class ContainerRelay extends ContainerNoInventoryTwoPort<TileRelay> implements ISEButtonEventHandler {
    @ContainerSynchronizer.SyncField
    public volatile double resistance;
    @ContainerSynchronizer.SyncField
    public volatile boolean isOn;
    @ContainerSynchronizer.SyncField
    public volatile double current;
    @ContainerSynchronizer.SyncField
    public volatile Direction inputSide, outputSide;

    // Server side
    public ContainerRelay(TileRelay tileEntity, int windowId, Player player) {
    	super(tileEntity, windowId, player);
    }

    // Client side
    public ContainerRelay(int windowId, Inventory inv) {
    	this(null, windowId, null);
    }

    @Override
    public void onButtonPressed(int buttonID, boolean isCtrlPressed) {
        double resistance = host.resistance;

        switch (buttonID) {
            case 0:
                resistance -= 1;
                break;
            case 1:
                resistance -= 0.1;
                break;
            case 2:
                if (isCtrlPressed)
                    resistance -= 0.001;
                else
                    resistance -= 0.01;
                break;
            case 3:
                if (isCtrlPressed)
                    resistance += 0.001;
                else
                    resistance += 0.01;
                break;
            case 4:
                resistance += 0.1;
                break;
            case 5:
                resistance += 1;
                break;

            default:
        }

        if (resistance < 0.001)
            resistance = 0.001;
        if (resistance > 100)
            resistance = 100;

        host.resistance = resistance;
    }
}
