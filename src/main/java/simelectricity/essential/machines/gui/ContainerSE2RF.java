package simelectricity.essential.machines.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.gui.AutoGuiHandler;
import simelectricity.essential.common.ContainerNoInvAutoSync;
import simelectricity.essential.machines.tile.TileSE2RF;
import simelectricity.essential.utils.network.ISEButtonEventHandler;

@AutoGuiHandler.Marker(GuiSE2RF.class)
public class ContainerSE2RF extends ContainerNoInvAutoSync<TileSE2RF> implements ISEButtonEventHandler {
	@ContainerSynchronizer.SyncField
	public double bufferedEnergy;

    @ContainerSynchronizer.SyncField
    public double voltage;
    @ContainerSynchronizer.SyncField
    public double actualInputPower;
    @ContainerSynchronizer.SyncField
    public int rfDemandRateDisplay;
    @ContainerSynchronizer.SyncField
    public int rfOutputRateDisplay;

    @ContainerSynchronizer.SyncField
    public double ratedOutputPower;

    // Server side
    public ContainerSE2RF(TileSE2RF tileEntity, int windowId, Player player) {
		super(tileEntity, windowId, player);
	}

    // Client side
    public ContainerSE2RF(int windowId, Inventory inv) {
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
        if (ratedOutputPower > TileSE2RF.bufferCapacity / 2)
            ratedOutputPower = TileSE2RF.bufferCapacity / 2;

        host.ratedOutputPower = ratedOutputPower;

        // SEAPI.energyNetAgent.updateTileParameter(this.host);
    }
}
