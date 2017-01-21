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

package simElectricity.Common.Items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.Items.ItemSE;
import simElectricity.API.SEEnergy;
import simElectricity.API.EnergyTile.*;
import simElectricity.API.SEAPI;

public class ItemUltimateMultimeter extends ItemSE {
    public ItemUltimateMultimeter() {
        super();
        maxStackSize = 1;
        setHasSubtypes(true);
        setUnlocalizedName("UltimateMultimeter");
        setMaxDamage(256);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister r) {
        itemIcon = r.registerIcon("simElectricity:Item_UltimateMultimeter");
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (world.isRemote)
        	return false;
       
        SEAPI.utils.chat(player, "------------------");       
        if (te instanceof ISEConductor) {
        	SEAPI.utils.chat(player, "Color: " + String.valueOf(((ISEConductor) te).getColor()) + ", " +
            				"Voltage: " + String.valueOf(SEEnergy.getVoltage(te)));
        }else if (te instanceof ISESimpleTile){
        	double voltage = SEEnergy.getVoltage(te);
        	double current = (voltage-((ISESimpleTile) te).getOutputVoltage())/((ISESimpleTile) te).getResistance();
        	SEAPI.utils.chat(player, "Internal Voltage: " + String.valueOf(((ISESimpleTile) te).getOutputVoltage()) + ", " +
    				"Voltage: " + String.valueOf(voltage)); 
        	SEAPI.utils.chat(player, "Resistance: " + String.valueOf(((ISESimpleTile) te).getResistance()) + ", " +
    				"Input current: " + String.valueOf(current));
        	SEAPI.utils.chat(player, "Input power: " + String.valueOf(current*voltage));
        }else if ((te instanceof ISESimulatable) && (!(world.isRemote))) {
        	String[] temp = te.toString().split("[.]");
        	SEAPI.utils.chat(player,  temp[temp.length-1].split("@")[0] + ": " + String.valueOf(SEEnergy.getVoltage(te)));
        }
        else if (te instanceof ISETile){
        	ISETile tile = (ISETile)te;
        	for (ForgeDirection dir : tile.getValidDirections()){
        		ISESubComponent comp = tile.getComponent(dir);
        		String[] temp = comp.toString().split("[.]");
        		SEAPI.utils.chat(player, temp[temp.length-1].split("@")[0] + ": " + String.valueOf(SEEnergy.getVoltage(comp, te.getWorldObj())));
        	}
        }else if (te instanceof ISEGridTile){
        	ISEGridNode gridNode = ((ISEGridTile)te).getGridNode();
        	if (gridNode == null){
        		SEAPI.utils.chat(player, "This gridTile has no corresponding gridNode! (BUG!)");
        	}else{
        		SEAPI.utils.chat(player, "voltage: " + String.valueOf(SEEnergy.getVoltage(gridNode, te.getWorldObj())));
        	}
        }
        
        return true;
        
    }
}
