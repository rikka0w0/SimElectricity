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

package simElectricity.Items;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import simElectricity.API.DataProvider.ISEVoltageSourceData;
import simElectricity.API.EnergyTile.*;
import simElectricity.API.Tile.*;
import simElectricity.API.SEAPI;

public class ItemUltimateMultimeter extends Item {
    public ItemUltimateMultimeter() {
    	setCreativeTab(SEAPI.SETab);
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

    /**
     * @param name name of this item.
     */
    @Override
    public Item setUnlocalizedName(String name) {
        GameRegistry.registerItem(this, name);
        return super.setUnlocalizedName(name);
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack itemStack) {
        return super.getUnlocalizedNameInefficiently(itemStack).replaceAll("item.", "item.sime:");
    }
    
    /**
     * Post some text in a player's chat window
     * 
     * @param player
     * @param text
     */
    public void chat(EntityPlayer player, String text) {
        player.addChatMessage(new ChatComponentText(text));
    }
    
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (world.isRemote)
        	return false;
        
        chat(player, "------------------");    
        if (te instanceof ISECableTile) {
        	ISESimulatable node = ((ISECableTile) te).getNode();
        	int color = ((ISECableTile) te).getColor();
        	chat(player, "Color: " + String.valueOf(color) + ", " +
            				"Voltage: " + String.valueOf(SEAPI.energyNetAgent.getVoltage(node)));
        	double currentMagnitude = SEAPI.energyNetAgent.getCurrentMagnitude(node);
        	if (!Double.isNaN(currentMagnitude))
        		chat(player, "Current: " + String.valueOf(currentMagnitude));
        }
        else if (te instanceof ISETile){
        	ISETile tile = (ISETile)te;
        	ForgeDirection[] dirs = tile.getValidDirections();
        	/*
        	if (dirs.length == 1 && tile.getComponent(dirs[0]).getDataProvider() instanceof ISEVoltageSourceData){
        		ISESubComponent vs = tile.getComponent(dirs[0]);
        		ISEVoltageSourceData data = (ISEVoltageSourceData) tile.getComponent(dirs[0]).getDataProvider();
                double voltage = SEAPI.energyNetAgent.getVoltage(vs);
                double current = (voltage-data.getOutputVoltage())/data.getResistance();
                chat(player, "Internal Voltage: " + String.valueOf(data.getOutputVoltage()) + ", " +
            				"Voltage: " + String.valueOf(voltage)); 
                chat(player, "Resistance: " + String.valueOf(data.getResistance()) + ", " +
            				"Input current: " + String.valueOf(current));
                chat(player, "Input power: " + String.valueOf(current*voltage));
        	}
        	else 
        	*/
        	for (ForgeDirection dir : tile.getValidDirections()){
        		ISESubComponent comp = tile.getComponent(dir);
        		String[] temp = comp.toString().split("[.]");
        		chat(player, temp[temp.length-1].split("@")[0] + ": " + String.valueOf(SEAPI.energyNetAgent.getVoltage(comp)));
        	}
        }else if (te instanceof ISEGridTile){
    		ISEGridNode comp = ((ISEGridTile) te).getGridNode();
    		String[] temp = comp.toString().split("[.]");
    		chat(player, temp[temp.length-1].split("@")[0] + ": " + String.valueOf(SEAPI.energyNetAgent.getVoltage(comp)));
        	double currentMagnitude = SEAPI.energyNetAgent.getCurrentMagnitude(comp);
        	if (!Double.isNaN(currentMagnitude))
        		chat(player, "Current: " + String.valueOf(currentMagnitude));
        }
        
        return true;
        
    }
}
