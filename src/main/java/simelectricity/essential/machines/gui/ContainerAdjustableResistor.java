package simelectricity.essential.machines.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.gui.AutoGuiHandler;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.ContainerNoInvAutoSync;
import simelectricity.essential.machines.tile.TileAdjustableResistor;
import simelectricity.essential.utils.network.ISEButtonEventHandler;

@AutoGuiHandler.Marker(GuiAdjustableResistor.class)
public class ContainerAdjustableResistor extends ContainerNoInvAutoSync<TileAdjustableResistor> implements ISEButtonEventHandler {
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

    // Server side
    public ContainerAdjustableResistor(TileAdjustableResistor tileEntity, int windowId, Player player) {
    	super(tileEntity, windowId, player);
    }

    // Client side
    public ContainerAdjustableResistor(int windowId, Inventory inv) {
    	this(null, windowId, null);
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
}
