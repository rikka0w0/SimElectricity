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
import simElectricity.API.ISidedFacing;
import simElectricity.API.Util;

public class ItemGlove extends ItemSE {
    public ItemGlove() {
        super();
        maxStackSize = 1;
        setHasSubtypes(true);
        setUnlocalizedName("Glove");
        setMaxDamage(256);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister r) {
        itemIcon = r.registerIcon("simElectricity:Item_Glove");
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if ((world.getTileEntity(x, y, z) instanceof ISidedFacing) & (!world.isRemote)) {
            ISidedFacing te = (ISidedFacing) world.getTileEntity(x, y, z);
            ForgeDirection newFacing = ForgeDirection.getOrientation(side);

            if (te.canSetFacing(newFacing)) {
                te.setFacing(newFacing);
                Util.updateFacing((TileEntity) te);
                itemStack.damageItem(1, player);
            }
            return true;
        } else {
            return false;
        }
    }
}
