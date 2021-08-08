package simelectricity.essential.machines.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.gui.AutoGuiHandler;
import simelectricity.essential.common.ContainerNoInvAutoSync;
import simelectricity.essential.machines.tile.TileVoltageMeter;

@AutoGuiHandler.Marker(GuiVoltageMeter.class)
public class ContainerVoltageMeter extends ContainerNoInvAutoSync<TileVoltageMeter> {
	@ContainerSynchronizer.SyncField
    public double voltage;

    // Server side
    public ContainerVoltageMeter(TileVoltageMeter tileEntity, int windowId, Player player) {
    	super(tileEntity, windowId, player);
    }

    // Client side
    public ContainerVoltageMeter(int windowId, Inventory inv) {
    	this(null, windowId, null);
    }
}
