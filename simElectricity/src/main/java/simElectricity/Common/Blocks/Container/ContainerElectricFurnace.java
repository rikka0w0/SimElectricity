package simElectricity.Common.Blocks.Container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntity;
import simElectricity.API.Common.ContainerBase;
import simElectricity.Common.Blocks.TileEntity.TileElectricFurnace;

public class ContainerElectricFurnace extends ContainerBase {
    public ContainerElectricFurnace(InventoryPlayer inventoryPlayer, TileEntity te) {
        super(inventoryPlayer, te);
    }

    protected int progress;


    @Override
    public void addCraftingToCrafters(ICrafting par1iCrafting) {
        super.addCraftingToCrafters(par1iCrafting);
        par1iCrafting.sendProgressBarUpdate(this, 0, ((TileElectricFurnace) tileEntity).progress);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2) {
        if (par1 == 0) ((TileElectricFurnace) tileEntity).progress = par2;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (Object crafter : this.crafters) {
            ICrafting var2 = (ICrafting) crafter;
            var2.sendProgressBarUpdate(this, 0, progress);
        }

        progress = ((TileElectricFurnace) tileEntity).progress;
    }

    @Override
    public int getPlayerInventoryStartIndex() {
        return 2;
    }

    @Override
    public int getPlayerInventoryEndIndex() {
        return 38;
    }

    @Override
    public int getTileInventoryStartIndex() {
        return 0;
    }

    @Override
    public int getTileInventoryEndIndex() {
        return 1;
    }

    @Override
    public void init() {
        addSlotToContainer(new Slot((IInventory) tileEntity, 0, 43, 33) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(itemStack);
                return result != null;
            }
        });
        addSlotToContainer(new Slot((IInventory) tileEntity, 1, 103, 34) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return false;
            }
        });
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
        ItemStack itemStack = null;
        Slot slotObject = (Slot) inventorySlots.get(slot);

        //null checks and checks if the item can be stacked (maxStackSize > 1)
        if (slotObject != null && slotObject.getHasStack()) {
            ItemStack stackInSlot = slotObject.getStack();
            itemStack = stackInSlot.copy();

            //merges the item into player inventory since its in the tileEntity
            if (slot < getPlayerInventoryStartIndex()) {
                if (!this.mergeItemStack(stackInSlot, getPlayerInventoryStartIndex(), getPlayerInventoryEndIndex(), true))
                    return null;
            }
            //places it into the tileEntity is possible since its in the player inventory
            else if (FurnaceRecipes.smelting().getSmeltingResult(stackInSlot) != null) {// one input slot only!!!!
                if (!this.mergeItemStack(stackInSlot, getTileInventoryStartIndex(), getTileInventoryEndIndex(), false))// input slot
                    return null;
            }// else if(remaining slots)   for multiple input slots

            if (stackInSlot.stackSize == 0)
                slotObject.putStack(null);
            else
                slotObject.onSlotChanged();

            if (stackInSlot.stackSize == itemStack.stackSize)
                return null;

            slotObject.onPickupFromSlot(player, stackInSlot);
        }
        return itemStack;
    }
}