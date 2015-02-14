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
import simElectricity.API.Energy;
import simElectricity.API.EnergyTile.IEnergyTile;
import simElectricity.API.Network;

public class ItemWrench extends ItemSE {
    public ItemWrench() {
        super();
        maxStackSize = 1;
        setHasSubtypes(true);
        setUnlocalizedName("Wrench");
        setMaxDamage(256);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if ((world.getTileEntity(pos) instanceof IEnergyTile) && (!world.isRemote)) {
            IEnergyTile te = (IEnergyTile) world.getTileEntity(pos);

            if (te.canSetFunctionalSide(side)) {
                te.setFunctionalSide(side);
                Energy.postTileRejoinEvent((TileEntity) te);
                Network.updateFunctionalSide((TileEntity) te);
                world.notifyNeighborsOfStateChange(pos, null);
                stack.damageItem(1, player);
            }

            return true;
        } else {
            return false;
        }
    }
}
