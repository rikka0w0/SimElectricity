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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.ISEWrenchable;
import simElectricity.API.ITileRenderingInfoSyncHandler;
import simElectricity.API.SEAPI;

public class ItemWrench extends Item {
    public ItemWrench() {
    	setCreativeTab(SEAPI.SETab);
        maxStackSize = 1;
        setHasSubtypes(true);
        setUnlocalizedName("Wrench");
        setMaxDamage(256);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister r) {
        itemIcon = r.registerIcon("simElectricity:Item_Wrench");
    }

    @Override
    public Item setUnlocalizedName(String name) {
        GameRegistry.registerItem(this, name);
        return super.setUnlocalizedName(name);
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack itemStack) {
        return super.getUnlocalizedNameInefficiently(itemStack).replaceAll("item.", "item.sime:");
    }
    
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if ((world.getTileEntity(x, y, z) instanceof ISEWrenchable) & (!world.isRemote)) {	//Server side only!
        	ISEWrenchable te = (ISEWrenchable) world.getTileEntity(x, y, z);
            ForgeDirection newFacing = ForgeDirection.getOrientation(side);

            if (te.canSetFunctionalSide(newFacing)) {
                te.setFunctionalSide(newFacing);
            }

            return true;
        } else {
            return false;
        }
    }
}
