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

package simElectricity.Templates.Container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import simElectricity.Templates.Common.ContainerBase;
import simElectricity.Templates.TileEntity.TileSimpleGenerator;

public class ContainerSimpleGenerator extends ContainerBase {
    public ContainerSimpleGenerator(InventoryPlayer inventoryPlayer, TileEntity te) {
        super(inventoryPlayer, te);
    }

    protected int progress, outputVoltage, outputResistance;

    @Override
    public void addCraftingToCrafters(ICrafting par1iCrafting) {
        super.addCraftingToCrafters(par1iCrafting);
        par1iCrafting.sendProgressBarUpdate(this, 0, ((TileSimpleGenerator) tileEntity).progress);
        par1iCrafting.sendProgressBarUpdate(this, 1, (int) ((TileSimpleGenerator) tileEntity).outputVoltage);
        par1iCrafting.sendProgressBarUpdate(this, 2, (int) ((TileSimpleGenerator) tileEntity).outputResistance);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2) {
        if (par1 == 0) ((TileSimpleGenerator) tileEntity).progress = par2;
        if (par1 == 1) ((TileSimpleGenerator) tileEntity).outputVoltage = par2;
        if (par1 == 2) ((TileSimpleGenerator) tileEntity).outputResistance = par2;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (Object crafter : this.crafters) {
            ICrafting var2 = (ICrafting) crafter;
            var2.sendProgressBarUpdate(this, 0, progress);
            var2.sendProgressBarUpdate(this, 1, outputVoltage);
            var2.sendProgressBarUpdate(this, 2, outputResistance);
        }

        progress = ((TileSimpleGenerator) tileEntity).progress;
        outputVoltage = (int) ((TileSimpleGenerator) tileEntity).outputVoltage;
        outputResistance = (int) ((TileSimpleGenerator) tileEntity).outputResistance;
    }

    @Override
    public int getPlayerInventoryStartIndex() {
        return 1;
    }

    @Override
    public int getPlayerInventoryEndIndex() {
        return 37;
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
        addSlotToContainer(new Slot((IInventory) tileEntity, 0, 51, 33) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return TileEntityFurnace.isItemFuel(itemStack);
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
            else if (TileEntityFurnace.isItemFuel(itemStack)) {// one input slot only!!!!
                if (!this.mergeItemStack(stackInSlot, getTileInventoryStartIndex(), getTileInventoryEndIndex(), false))// input slot
                    return null;
            }// else if(remaining slots)   for multiple input slots, waiting for the interface to simplify

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
