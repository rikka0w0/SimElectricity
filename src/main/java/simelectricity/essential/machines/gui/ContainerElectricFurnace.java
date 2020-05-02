package simelectricity.essential.machines.gui;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.*;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import rikka.librikka.container.ContainerInventory;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.gui.AutoGuiHandler;
import simelectricity.essential.Essential;
import simelectricity.essential.machines.tile.TileElectricFurnace;
import simelectricity.essential.utils.network.MessageContainerSync;

import java.util.Iterator;
import java.util.Optional;

@AutoGuiHandler.Marker(GuiElectricFurnace.class)
public class ContainerElectricFurnace extends ContainerInventory {
    @ContainerSynchronizer.SyncField
    public int progress;
    
    // Client side
    public ContainerElectricFurnace(int windowId, PlayerInventory playerInv, PacketBuffer data) {
    	this(windowId, playerInv, new Inventory(2));
    }
    
    // Server side
    public ContainerElectricFurnace(int windowId, PlayerInventory playerInv, IInventory machineInv) {
        super(Essential.MODID, windowId, playerInv, machineInv);

        addSlot(new Slot(inventoryTile, 0, 43, 33) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
            	World world = playerInv.player.world;
                Optional<FurnaceRecipe> result = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(itemStack), world);
                return result.isPresent();
            }
        });

        addSlot(new Slot(inventoryTile, 1, 103, 34) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return false;
            }
        });
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        Object[] changeList = ContainerSynchronizer.detectChanges(this, ContainerInventory.class, inventoryTile);

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
