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

package simelectricity.Templates.Container;

import java.util.Iterator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import simelectricity.Templates.Common.ContainerBase;
import simelectricity.Templates.Common.ProductSlot;
import simelectricity.Templates.TileEntity.TileIceMachine;

public class ContainerIceMachine extends ContainerBase<TileIceMachine>{
	public int progress,fluidID,amountP, isPowered;
	
    public ContainerIceMachine(InventoryPlayer inventoryPlayer, TileEntity te) {
        super(inventoryPlayer, te);
    }
    
    @Override
    public void init() {
        addSlotToContainer(new Slot(tileEntity, 0, 14, 18));
        addSlotToContainer(new ProductSlot(tileEntity, 1, 14, 49));
        addSlotToContainer(new ProductSlot(tileEntity, 2, 134, 34));
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
    	par1iCrafting.sendProgressBarUpdate(this, 0, tileEntity.progress);
    	par1iCrafting.sendProgressBarUpdate(this, 1, tileEntity.fluidID);
    	par1iCrafting.sendProgressBarUpdate(this, 2, tileEntity.amountP);
    	par1iCrafting.sendProgressBarUpdate(this, 3, tileEntity.isPowered);
    }
    
    
    @Override
	@SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2)
    {
    	switch(par1){
    		case 0:{tileEntity.progress = par2;return;}
    		case 1:{tileEntity.fluidID = par2;return;}    		
    		case 2:{tileEntity.amountP = par2;return;}   
    		case 3:{tileEntity.isPowered = par2;return;}       		
    	}
    	
   	}
    
    @Override
    public void detectAndSendChanges()
    {
    	super.detectAndSendChanges();
    	Iterator var1 = this.crafters.iterator();
    	while (var1.hasNext())
    	{
    		ICrafting var2 = (ICrafting)var1.next();
    		var2.sendProgressBarUpdate(this, 0, progress);
    		var2.sendProgressBarUpdate(this, 1, fluidID);     
    		var2.sendProgressBarUpdate(this, 2, amountP);
    		var2.sendProgressBarUpdate(this, 3, isPowered);
    	}
    	
    	progress = tileEntity.progress;
    	fluidID = tileEntity.fluidID;
    	amountP = tileEntity.amountP;
    	isPowered = tileEntity.isPowered;
    }
}
