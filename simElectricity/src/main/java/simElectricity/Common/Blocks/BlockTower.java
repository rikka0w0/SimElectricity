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

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import simElectricity.API.Common.Blocks.BlockContainerSE;
import simElectricity.API.Util;
import simElectricity.Common.Blocks.TileEntity.TileTower;
import simElectricity.Common.Items.ItemBlocks.ItemBlockTower;

public class BlockTower extends BlockContainerSE {
	public static final String[] subNames = {"0","1"};

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List subItems) {
        for (int ix = 0; ix < subNames.length; ix++) {
            subItems.add(new ItemStack(this, 1, ix));
        }
    }

    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ItemBlockTower.class, name);
        return super.setBlockName(name);
    }

    @Override
    public boolean shouldRegister() {
        return false;
    }
	
    public BlockTower() {
        super();
        setBlockName("Tower");
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        if (world.isRemote)
            return;

        TileTower tower = (TileTower) world.getTileEntity(x, y, z);
        tower.facing = Util.getPlayerSight(player, true).ordinal();
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
