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

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simElectricity.API.Common.ContainerBase;
import simElectricity.API.ProductSlot;
import simElectricity.Common.Blocks.TileEntity.TileIceMachine;

import java.util.Iterator;

public class ContainerIceMachine extends ContainerBase {
    public int progress, fluidID, amountP, isPowered;

    public ContainerIceMachine(InventoryPlayer inventoryPlayer, TileEntity te) {
        super(inventoryPlayer, te);
    }

    @Override
    public void init() {
        addSlotToContainer(new Slot((IInventory) tileEntity, 0, 14, 18));
        addSlotToContainer(new ProductSlot((IInventory) tileEntity, 1, 14, 49));
        addSlotToContainer(new ProductSlot((IInventory) tileEntity, 2, 134, 34));
    }

    @Override
    public int getPlayerInventoryStartIndex() {
        return 3;
    }

    @Override
    public int getPlayerInventoryEndIndex() {
        return 39;
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
    public void addCraftingToCrafters(ICrafting par1iCrafting) {
        super.addCraftingToCrafters(par1iCrafting);
        par1iCrafting.sendProgressBarUpdate(this, 0, ((TileIceMachine) tileEntity).progress);
        par1iCrafting.sendProgressBarUpdate(this, 1, ((TileIceMachine) tileEntity).fluidID);
        par1iCrafting.sendProgressBarUpdate(this, 2, ((TileIceMachine) tileEntity).amountP);
        par1iCrafting.sendProgressBarUpdate(this, 3, ((TileIceMachine) tileEntity).isPowered);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2) {
        switch (par1) {
            case 0: {
                ((TileIceMachine) tileEntity).progress = par2;
                return;
            }
            case 1: {
                ((TileIceMachine) tileEntity).fluidID = par2;
                return;
            }
            case 2: {
                ((TileIceMachine) tileEntity).amountP = par2;
                return;
            }
            case 3: {
                ((TileIceMachine) tileEntity).isPowered = par2;
                return;
            }
        }

    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        Iterator var1 = this.crafters.iterator();
        while (var1.hasNext()) {
            ICrafting var2 = (ICrafting) var1.next();
            var2.sendProgressBarUpdate(this, 0, progress);
            var2.sendProgressBarUpdate(this, 1, fluidID);
            var2.sendProgressBarUpdate(this, 2, amountP);
            var2.sendProgressBarUpdate(this, 3, isPowered);
        }

        progress = ((TileIceMachine) tileEntity).progress;
        fluidID = ((TileIceMachine) tileEntity).fluidID;
        amountP = ((TileIceMachine) tileEntity).amountP;
        isPowered = ((TileIceMachine) tileEntity).isPowered;
    }
}
