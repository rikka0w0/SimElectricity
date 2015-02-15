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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import simElectricity.API.Common.Items.ItemSE;
import simElectricity.API.ISidedFacing;
import simElectricity.API.Network;

public class ItemGlove extends ItemSE {
    public ItemGlove() {
        super();
        maxStackSize = 1;
        setHasSubtypes(true);
        setUnlocalizedName("glove");
        setMaxDamage(256);
    }


    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if ((world.getTileEntity(pos) instanceof ISidedFacing) & (!world.isRemote)) {
            ISidedFacing te = (ISidedFacing) world.getTileEntity(pos);

            if (te.canSetFacing(side)) {
                te.setFacing(side);
                Network.updateFacing((TileEntity) te);
                itemStack.damageItem(1, player);
            }
            return true;
        } else {
            return false;
        }
    }
}
