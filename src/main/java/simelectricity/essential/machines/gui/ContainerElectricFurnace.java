package simelectricity.essential.machines.gui;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.*;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import rikka.librikka.container.ContainerInventory;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.gui.AutoGuiHandler;
import simelectricity.essential.Essential;
import simelectricity.essential.machines.tile.TileElectricFurnace;
import simelectricity.essential.utils.network.MessageContainerSync;

import java.util.Iterator;

@AutoGuiHandler.Marker(GuiElectricFurnace.class)
public class ContainerElectricFurnace extends ContainerInventory<IInventory> {
	private TileElectricFurnace te;
    @ContainerSynchronizer.SyncField
    public int progress;
    
    // Client side
    public ContainerElectricFurnace(int windowId, PlayerInventory playerInv, PacketBuffer data) {
    	this(windowId, playerInv, new Inventory(2));
    	this.te = null;
    }
    
    // Server side
    public ContainerElectricFurnace(int windowId, PlayerInventory playerInv, TileElectricFurnace te) {
    	this(windowId, playerInv, te.inventory);
    	this.te = te;
    }
    
    // Common
    private ContainerElectricFurnace(int windowId, PlayerInventory playerInv, IInventory machineInv) {
        super(Essential.MODID, windowId, playerInv, machineInv);
        addSlot(new Slot(machineInv, 0, 43, 33) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
            	return machineInv.isItemValidForSlot(0, itemStack);
            }
        });

        addSlot(new Slot(machineInv, 1, 103, 34) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return false;
            }
        });
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        Object[] changeList = ContainerSynchronizer.detectChanges(this, ContainerInventory.class, te);

        if (changeList == null)
            return;

        Iterator<IContainerListener> iterator = getListeners().iterator();
        while (iterator.hasNext()) {
            IContainerListener crafter = iterator.next();

            if (crafter instanceof ServerPlayerEntity) {
                MessageContainerSync.syncToClient((ServerPlayerEntity) crafter, changeList);
            }
        }
    }
}
