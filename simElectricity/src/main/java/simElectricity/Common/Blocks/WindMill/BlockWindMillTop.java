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

package simElectricity.Common.Blocks.WindMill;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import simElectricity.API.Common.Blocks.BlockContainerSE;
import simElectricity.API.ISidedFacing;
import simElectricity.API.Network;
import simElectricity.API.Util;
import simElectricity.Common.Core.SEItems;

public class BlockWindMillTop extends BlockContainerSE {


    public BlockWindMillTop() {
        super();
        setUnlocalizedName("windmill_top");
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileWindMillTop te = (TileWindMillTop) world.getTileEntity(pos);
        ItemStack playerItem = player.getCurrentEquippedItem();

        if (player.isSneaking())
            return false;


        if (te.settled) {
            if (playerItem != null)
                return false;

//            dropBlockAsItem(world, pos.add(0, 1, 0), state, new ItemStack(SEItems.fan, 1), 1);

            te.settled = false;
            if (!world.isRemote)
                Network.updateTileEntityFields(te, "settled");
        } else {
            if (playerItem == null)
                return false;

            if (playerItem.getItem() != SEItems.fan)
                return false;

            te.settled = true;
            if (!world.isRemote)
                Network.updateTileEntityFields(te, "settled");
        }

        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof TileWindMillTop) {
//            if (((TileWindMillTop) te).settled)
//                dropBlockAsItem(world, x, y, z, new ItemStack(SEItems.fan, 1));
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        TileEntity te = world.getTileEntity(pos);

        ((ISidedFacing) te).setFacing(Util.getPlayerSight(player, true).getOpposite());
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileWindMillTop();
    }
}
