package simelectricity.essential.coverpanel;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.gui.AutoGuiHandler;
import simelectricity.essential.common.ContainerNoInvAutoSync;
import simelectricity.essential.utils.network.ISEButtonEventHandler;

@AutoGuiHandler.Marker(GuiVoltageSensor.class)
public class ContainerVoltageSensor extends ContainerNoInvAutoSync<VoltageSensorPanel> implements ISEButtonEventHandler {
	@ContainerSynchronizer.SyncField
	public boolean emitRedStoneSignal;
    @ContainerSynchronizer.SyncField
    public boolean inverted;
    @ContainerSynchronizer.SyncField
    public double thresholdVoltage;

    // Server side
    public ContainerVoltageSensor(VoltageSensorPanel panel, int windowId, Player player) {
    	super(panel, windowId, player);
    }

    // Client side
    public ContainerVoltageSensor(int windowId, Inventory inv) {
    	this(null, windowId, null);
    }

    @Override
    public void onButtonPressed(int buttonID, boolean isCtrlPressed) {
        double thresholdVoltage = this.thresholdVoltage;
        boolean inverted = this.inverted;

        switch (buttonID) {
            case 0:
                thresholdVoltage -= 100;
                break;
            case 1:
                thresholdVoltage -= 10;
                break;
            case 2:
                if (isCtrlPressed)
                    thresholdVoltage -= 0.1;
                else
                    thresholdVoltage -= 1;
                break;
            case 3:
                if (isCtrlPressed)
                    thresholdVoltage += 0.1;
                else
                    thresholdVoltage += 1;
                break;
            case 4:
                thresholdVoltage += 10;
                break;
            case 5:
                thresholdVoltage += 100;
                break;

            case 6:
                inverted = !inverted;
                break;
        }

        if (thresholdVoltage < 0.1)
            thresholdVoltage = 0.1;
        if (thresholdVoltage > 500)
            thresholdVoltage = 500;

        this.host.thresholdVoltage = thresholdVoltage;
        this.host.inverted = inverted;

        this.host.checkRedStoneSignal();
    }
}
