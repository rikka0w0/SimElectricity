package simelectricity.essential.machines.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.gui.AutoGuiHandler;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.ContainerNoInventoryTwoPort;
import simelectricity.essential.machines.tile.TileCurrentSensor;
import simelectricity.essential.utils.network.ISEButtonEventHandler;

@AutoGuiHandler.Marker(GuiCurrentSensor.class)
public class ContainerCurrentSensor extends ContainerNoInventoryTwoPort<TileCurrentSensor> implements ISEButtonEventHandler {
	@ContainerSynchronizer.SyncField
	public double thresholdCurrent, resistance;
	@ContainerSynchronizer.SyncField
	public Direction inputSide, outputSide;
	@ContainerSynchronizer.SyncField
	public boolean absMode, inverted;
	@ContainerSynchronizer.SyncField
    public double current;
	@ContainerSynchronizer.SyncField
    public boolean emitRedstoneSignal;

    // Server side
    public ContainerCurrentSensor(TileCurrentSensor tileEntity, int windowId, Player player) {
    	super(tileEntity, windowId, player);
    }

    // Client side
    public ContainerCurrentSensor(int windowId, Inventory inv) {
    	this(null, windowId, null);
    }

    @Override
    public void onButtonPressed(int buttonID, boolean isCtrlPressed) {
        double resistance = host.resistance;
        double thresholdCurrent = host.thresholdCurrent;
        boolean absMode = host.absMode;
        boolean inverted = host.inverted;

        switch (buttonID) {
            case 0:
                thresholdCurrent -= 100;
                break;
            case 1:
                thresholdCurrent -= 10;
                break;
            case 2:
                if (isCtrlPressed)
                    thresholdCurrent -= 0.1;
                else
                    thresholdCurrent -= 1;
                break;
            case 3:
                if (isCtrlPressed)
                    thresholdCurrent += 0.1;
                else
                    thresholdCurrent += 1;
                break;
            case 4:
                thresholdCurrent += 10;
                break;
            case 5:
                thresholdCurrent += 100;
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
                inverted = !inverted;
                break;

            case 13:
                absMode = !absMode;
                break;
            default:
        }

        if (resistance < 0.001)
            resistance = 0.001;
        if (resistance > 100)
            resistance = 100;

        if (thresholdCurrent < 0.1)
            thresholdCurrent = 0.1;
        if (thresholdCurrent > 1000)
            thresholdCurrent = 1000;

        if (this.host.resistance != resistance) {
            this.host.resistance = resistance;
            SEAPI.energyNetAgent.updateTileParameter(host);
        }

        if (host.thresholdCurrent != thresholdCurrent) {
            host.thresholdCurrent = thresholdCurrent;
            this.host.checkRedstoneStatus();
        }

        if (this.host.inverted != inverted) {
            this.host.inverted = inverted;
            this.host.checkRedstoneStatus();
        }

        if (this.host.absMode != absMode) {
            this.host.absMode = absMode;
            this.host.checkRedstoneStatus();
        }
    }
}
