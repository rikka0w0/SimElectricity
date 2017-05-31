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

import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.tileentity.TileEntity;
import simElectricity.Templates.Common.ContainerBase;
import simElectricity.Templates.TileEntity.TileSwitch;
import simElectricity.Templates.Utils.MessageGui;

public class ContainerSwitch extends ContainerBase<TileSwitch> {
    public ContainerSwitch(InventoryPlayer inventoryPlayer, TileEntity te) {
        super(inventoryPlayer, te);
    }

    @Override
    public int getPlayerInventoryStartIndex() {
        return 27;
    }

    @Override
    public int getPlayerInventoryEndIndex() {
        return 36;
    }

    @Override
    public int getTileInventoryStartIndex() {
        return 0;
    }

    @Override
    public int getTileInventoryEndIndex() {
        return 27;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        
    	Iterator iterator = this.crafters.iterator();
    	
    	while (iterator.hasNext())
    	{
    		ICrafting crafting = (ICrafting)iterator.next();
    		MessageGui.sendToGui((EntityPlayerMP)crafting, (byte)0
    							, tileEntity.current
    							, tileEntity.maxCurrent
    							, tileEntity.isOn
    							, tileEntity.inputSide
    							, tileEntity.outputSide
    							, tileEntity.resistance
    							);
    	}
    }
}