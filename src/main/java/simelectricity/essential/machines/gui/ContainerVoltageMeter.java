package simelectricity.essential.machines.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.gui.AutoGuiHandler;
import simelectricity.essential.common.ContainerNoInvAutoSync;
import simelectricity.essential.machines.tile.TileVoltageMeter;

@AutoGuiHandler.Marker(GuiVoltageMeter.class)
public class ContainerVoltageMeter extends ContainerNoInvAutoSync<TileVoltageMeter> {
	@ContainerSynchronizer.SyncField
    public double voltage;

    // Server side
    public ContainerVoltageMeter(TileVoltageMeter tileEntity, int windowId) {
    	super(tileEntity, windowId);
    }

    // Client side
    public ContainerVoltageMeter(int windowId, PlayerInventory inv, PacketBuffer data) {
    	this(null, windowId);
    }
}
