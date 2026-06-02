package simelectricity.essential.machines.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.gui.AutoGuiHandler;
import simelectricity.essential.common.ContainerNoInventoryTwoPort;
import simelectricity.essential.machines.blockentity.BlockEntityDiode;

@AutoGuiHandler.Marker(GuiDiode.class)
public class ContainerDiode extends ContainerNoInventoryTwoPort<BlockEntityDiode> {
	@ContainerSynchronizer.SyncField
    public double inputVoltage, outputVoltage;
    @ContainerSynchronizer.SyncField
    public Direction inputSide, outputSide;

    // Server side
    public ContainerDiode(BlockEntityDiode tileEntity, int windowId, Player player) {
    	super(tileEntity, windowId, player);
    }

    // Client side
    public ContainerDiode(int windowId, Inventory inv) {
    	this(null, windowId, null);
    }
}
