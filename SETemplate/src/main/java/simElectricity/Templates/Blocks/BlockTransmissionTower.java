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

package simElectricity.Templates.Blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import simElectricity.API.SEAPI;

import simElectricity.Templates.Common.BlockContainerSE;
import simElectricity.Templates.ItemBlocks.ItemBlockTransmissionTower;
import simElectricity.Templates.TileEntity.TileTransmissionTower;

import java.util.List;

public class BlockTransmissionTower extends BlockContainerSE {
	public static final String[] subNames = {"0","1","2"};

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
        GameRegistry.registerBlock(this, ItemBlockTransmissionTower.class, name);
        return super.setBlockName(name);
    }

    @Override
    public boolean shouldRegister() {
        return false;
    }
	
    public BlockTransmissionTower() {
        super();
        setBlockName("BlockTransimissionTower");
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        if (world.isRemote)
            return;

        TileTransmissionTower tower = (TileTransmissionTower) world.getTileEntity(x, y, z);
        tower.facing = 8 - MathHelper.floor_double((player.rotationYaw) * 8.0F / 360.0F + 0.5D) & 7;

        SEAPI.energyNetAgent.attachGridObject(world, x, y, z, (byte)0);
        
        //SEAPI.networkManager.updateNetworkFields(tower);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
    	SEAPI.energyNetAgent.detachGridObject(world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileTransmissionTower();
    }

    //This will tell minecraft not to render any side of our cube.
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return false;
    }

    //And this tell it that you can see through this block, and neighbor blocks should be rendered.
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
