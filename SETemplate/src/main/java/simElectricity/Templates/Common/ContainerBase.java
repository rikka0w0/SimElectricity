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

package simElectricity.Templates.Common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public abstract class ContainerBase<TYPE extends TileEntity> extends Container {
    protected TYPE tileEntity;

    public void init(){}
    
    ///**
    // * Use for Server->Client sync, see MessageGui.sendToGui
    // */
    //public void prepareGui(EntityPlayerMP player){}
        
    public abstract int getPlayerInventoryStartIndex();

    public abstract int getPlayerInventoryEndIndex();

    public abstract int getTileInventoryStartIndex();

    public abstract int getTileInventoryEndIndex();

    public ContainerBase(InventoryPlayer inventoryPlayer, TileEntity te) {
        tileEntity = (TYPE) te;

        init();        
        //if (!tileEntity.getWorldObj().isRemote)	//Server
        //	prepareGui((EntityPlayerMP) inventoryPlayer.player);

        bindPlayerInventory(inventoryPlayer);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    public void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
                        8 + j * 18, 84 + i * 18));

        for (int i = 0; i < 9; i++)
            addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
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
            else if (!this.mergeItemStack(stackInSlot, getTileInventoryStartIndex(), getTileInventoryEndIndex(), false))
                return null;


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
