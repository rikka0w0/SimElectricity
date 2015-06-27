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

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import simElectricity.API.Common.Blocks.BlockStandardSEMachine;
import simElectricity.API.Common.Blocks.BlockStates;
import simElectricity.API.ISidedFacing;
import simElectricity.Common.Blocks.TileEntity.TileIncandescentLamp;

public class BlockIncandescentLamp extends BlockStandardSEMachine {

    public BlockIncandescentLamp() {
        super();
        setUnlocalizedName("incandescent_lamp");
    }

    @Override
    public int getLightValue(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileIncandescentLamp)
            return (((TileIncandescentLamp) tile).lightLevel);
        return 0;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileIncandescentLamp();
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[]{BlockStates.FACING, BlockStates.ISWORKING});
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof ISidedFacing) {
            state = state.withProperty(BlockStates.FACING, ((ISidedFacing) tile).getFacing());
        }
        if (tile instanceof TileIncandescentLamp) {
            state = state.withProperty(BlockStates.ISWORKING, ((TileIncandescentLamp) tile).lightLevel > 0);
        }
        return state;
    }
}
