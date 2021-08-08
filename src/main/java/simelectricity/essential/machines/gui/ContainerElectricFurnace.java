package simelectricity.essential.machines.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import rikka.librikka.container.ContainerInventory;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.gui.AutoGuiHandler;
import simelectricity.essential.Essential;
import simelectricity.essential.machines.tile.TileElectricFurnace;
import simelectricity.essential.utils.network.MessageContainerSync;

@AutoGuiHandler.Marker(GuiElectricFurnace.class)
public class ContainerElectricFurnace extends ContainerInventory<Container> {
	private TileElectricFurnace te;
    @ContainerSynchronizer.SyncField
    public int progress;

    protected final Player player;

    // Client side
    public ContainerElectricFurnace(int windowId, Inventory playerInv) {
    	this(windowId, playerInv, new SimpleContainer(2));
    	this.te = null;
    }

    // Server side
    public ContainerElectricFurnace(int windowId, Inventory playerInv, TileElectricFurnace te) {
    	this(windowId, playerInv, te.inventory);
    	this.te = te;
    }

    // Common
    private ContainerElectricFurnace(int windowId, Inventory playerInv, Container machineInv) {
        super(Essential.MODID, windowId, playerInv, machineInv);
        this.player = playerInv.player;

        addSlot(new Slot(machineInv, 0, 43, 33) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
            	return machineInv.canPlaceItem(0, itemStack);
            }
        });

        addSlot(new Slot(machineInv, 1, 103, 34) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return false;
            }
        });
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        Object[] changeList = ContainerSynchronizer.detectChanges(this, ContainerInventory.class, te);

        if (changeList == null)
            return;

        MessageContainerSync.syncToClient((ServerPlayer) this.player, changeList);
    }
}
