/*
 * Copyright (C) 2014 SimElectricity
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package simElectricity.Common.Blocks.Container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simElectricity.API.Common.ContainerBase;
import simElectricity.Common.Blocks.TileEntity.TileElectricFurnace;

public class ContainerElectricFurnace extends ContainerBase {
    protected int progress;

    public ContainerElectricFurnace(InventoryPlayer inventoryPlayer, TileEntity te) {
        super(inventoryPlayer, te);
    }

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
                ItemStack result = FurnaceRecipes.instance().getSmeltingResult(itemStack);
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
            else if (FurnaceRecipes.instance().getSmeltingResult(stackInSlot) != null) {// one input slot only!!!!
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