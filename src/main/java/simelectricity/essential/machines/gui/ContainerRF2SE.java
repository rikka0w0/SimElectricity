package simelectricity.essential.machines.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.gui.AutoGuiHandler;
import simelectricity.essential.common.ContainerNoInvAutoSync;
import simelectricity.essential.machines.tile.TileRF2SE;
import simelectricity.essential.utils.network.ISEButtonEventHandler;

@AutoGuiHandler.Marker(GuiRF2SE.class)
public class ContainerRF2SE extends ContainerNoInvAutoSync<TileRF2SE> implements ISEButtonEventHandler {
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

    // Server side
    public ContainerRF2SE(TileRF2SE tileEntity, int windowId, Player player) {
        super(tileEntity, windowId, player);
    }

    // Client side
    public ContainerRF2SE(int windowId, Inventory inv) {
    	this(null, windowId, null);
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
