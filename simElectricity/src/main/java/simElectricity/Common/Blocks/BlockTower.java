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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.Blocks.BlockContainerSE;
import simElectricity.API.Util;
import simElectricity.Common.Blocks.TileEntity.TileTower;

public class BlockTower extends BlockContainerSE {


    public BlockTower() {
        super(Material.iron);
        setHardness(2.0F);
        setResistance(5.0F);
        setBlockName("Tower");
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        if (world.isRemote)
            return;

        TileTower tower = (TileTower) world.getTileEntity(x, y, z);
        ForgeDirection playerSight = Util.getPlayerSight(player, false);
        if (playerSight == ForgeDirection.EAST || playerSight == ForgeDirection.WEST) {
            tower.facing = 1;
        } else {
            tower.facing = 0;
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (world.getTileEntity(x, y, z) instanceof TileTower) {
            TileTower tower = (TileTower) world.getTileEntity(x, y, z);
            for (int i = 0; i < tower.neighborsInfo.length; i += 3)
                if (world.getTileEntity(tower.neighborsInfo[i], tower.neighborsInfo[i + 1], tower.neighborsInfo[i + 2]) instanceof TileTower)
                    ((TileTower) world.getTileEntity(tower.neighborsInfo[i], tower.neighborsInfo[i + 1], tower.neighborsInfo[i + 2])).delNeighbor(tower);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileTower();
    }

    //This will tell minecraft not to render any side of our cube.
    @Override
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return false;
    }

    //And this tell it that you can see through this block, and neighbor blocks should be rendered.
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
