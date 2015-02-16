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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simElectricity.API.Common.Blocks.BlockStandardSEHoriMachine;
import simElectricity.API.Util;
import simElectricity.Common.Blocks.TileEntity.TileElectricFurnace;
import simElectricity.Common.Core.SEBlocks;
import simElectricity.Common.SEUtils;
import simElectricity.SimElectricity;

import java.util.Random;

public class BlockElectricFurnace extends BlockStandardSEHoriMachine {

    private static boolean keepInventory;
    public final boolean isBurning;


    public BlockElectricFurnace(boolean isBurning) {
        super();
        this.isBurning = isBurning;
        if (isBurning)
            setUnlocalizedName("electric_furnace_lit");
        else {
            setCreativeTab(Util.SETab);
            setUnlocalizedName("electric_furnace");
        }
    }

    public static void setState(boolean active, World world, BlockPos pos, TileElectricFurnace tileEntity) {
        IBlockState state = world.getBlockState(pos);
        SEUtils.logInfo("test");
        keepInventory = true;

        if (active) {
            world.setBlockState(pos, SEBlocks.electricFurnace_lit.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);
            world.setBlockState(pos, SEBlocks.electricFurnace_lit.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);
        } else {
            world.setBlockState(pos, SEBlocks.electricFurnace.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);
            world.setBlockState(pos, SEBlocks.electricFurnace.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);
        }

        keepInventory = false;

        if (tileEntity != null) {
            tileEntity.validate();
            world.setTileEntity(pos, tileEntity);
        }
    }

    @Override
    public int getLightValue(IBlockAccess world, BlockPos pos) {
        return isBurning ? 13 : 0;
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);

        if (player.isSneaking())
            return false;

        if (!(te instanceof TileElectricFurnace))
            return false;
        player.openGui(SimElectricity.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (this.isBurning) {
            EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);
            double d0 = (double) pos.getX() + 0.5D;
            double d1 = (double) pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
            double d2 = (double) pos.getZ() + 0.5D;
            double d3 = 0.52D;
            double d4 = rand.nextDouble() * 0.6D - 0.3D;

            switch (enumfacing) {
                case WEST:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 - d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;
                case EAST:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;
                case NORTH:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 - d3, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 - d3, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;
                case SOUTH:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 + d3, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 + d3, 0.0D, 0.0D, 0.0D, new int[0]);
            }
        }
    }

    @Override
    public boolean registerInCreativeTab() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileElectricFurnace();
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[]{FACING});
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!keepInventory) {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileElectricFurnace) {
                InventoryHelper.dropInventoryItems(worldIn, pos, (TileElectricFurnace) tileentity);
                worldIn.updateComparatorOutputLevel(pos, this);
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(SEBlocks.electricFurnace);
    }
}
