package simelectricity.essential.machines.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.gui.AutoGuiHandler;
import simelectricity.essential.common.ContainerNoInventoryTwoPort;
import simelectricity.essential.machines.tile.TileDiode;

@AutoGuiHandler.Marker(GuiDiode.class)
public class ContainerDiode extends ContainerNoInventoryTwoPort<TileDiode> {
	@ContainerSynchronizer.SyncField
    public double inputVoltage, outputVoltage;
    @ContainerSynchronizer.SyncField
    public Direction inputSide, outputSide;

    // Server side
    public ContainerDiode(TileDiode tileEntity, int windowId) {
    	super(tileEntity, windowId);
    }

    // Client side
    public ContainerDiode(int windowId, PlayerInventory inv, PacketBuffer data) {
    	this(null, windowId);
    }
}
