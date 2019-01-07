package simelectricity.essential.machines.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import rikka.librikka.container.ContainerInventory;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.container.IContainerWithGui;
import simelectricity.essential.machines.tile.TileElectricFurnace;
import simelectricity.essential.utils.network.MessageContainerSync;

import java.util.Iterator;

public class ContainerElectricFurnace extends ContainerInventory<TileElectricFurnace>  implements IContainerWithGui {
    @ContainerSynchronizer.SyncField
    public int progress;

    public ContainerElectricFurnace(InventoryPlayer playerInventory, TileElectricFurnace tileEntity) {
        super(playerInventory, tileEntity);

        addSlotToContainer(new Slot(inventoryTile, 0, 43, 33) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                ItemStack result = FurnaceRecipes.instance().getSmeltingResult(itemStack);
                return !result.isEmpty();
            }
        });

        addSlotToContainer(new Slot(inventoryTile, 1, 103, 34) {
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

        Iterator<IContainerListener> iterator = listeners.iterator();
        while (iterator.hasNext()) {
            IContainerListener crafter = iterator.next();

            if (crafter instanceof EntityPlayerMP) {
                MessageContainerSync.syncToClient((EntityPlayerMP) crafter, changeList);
            }
        }
    }

    @Override
    public GuiScreen createGui() {
        return new GuiElectricFurnace(this);
    }
}
