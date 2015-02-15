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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import simElectricity.API.Common.Blocks.AutoFacing;
import simElectricity.API.Common.Blocks.BlockStandardGenerator;
import simElectricity.API.Common.TileSidedGenerator;
import simElectricity.API.Util;
import simElectricity.Common.Blocks.TileEntity.TileSolarPanel;

public class BlockSolarPanel extends BlockStandardGenerator {

    public BlockSolarPanel() {
        super();
        setUnlocalizedName("solar_panel");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileSolarPanel();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(pos);

        if (!(te instanceof TileSidedGenerator))
            return;

        EnumFacing functionalSide = AutoFacing.autoConnect(te, Util.getPlayerSight(player, false).getOpposite(), EnumFacing.UP);
        if (functionalSide == EnumFacing.UP)
            functionalSide = EnumFacing.DOWN;

        ((TileSidedGenerator) te).setFunctionalSide(functionalSide);
    }


}
