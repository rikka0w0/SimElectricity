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

package simElectricity.Common.Blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import simElectricity.API.Common.Blocks.BlockContainerSE;
import simElectricity.API.Util;
import simElectricity.Common.Blocks.TileEntity.TileAdjustableTransformer;
import simElectricity.SimElectricity;

public class BlockAdjustableTransformer extends BlockContainerSE {

    public BlockAdjustableTransformer() {
        super();
        setUnlocalizedName("adjustable_transformer");
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking())
            return false;

        player.openGui(SimElectricity.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileAdjustableTransformer))
            return;
        ((TileAdjustableTransformer) te).secondarySide = Util.getPlayerSight(player, false);
        ((TileAdjustableTransformer) te).primarySide = ((TileAdjustableTransformer) te).secondarySide.getOpposite();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileAdjustableTransformer();
    }
}
