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

package simElectricity.API.Common.Blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import simElectricity.API.Common.Items.ItemBlockSE;
import simElectricity.API.INetworkEventHandler;
import simElectricity.API.Util;

import java.util.Random;

/**
 * Basic SimElectricity Container Block
 *
 * @author <Meow J>
 */
public abstract class BlockContainerSE extends BlockContainer {
    public BlockContainerSE(Material material) {
        super(material);
        if (registerInCreativeTab())
            setCreativeTab(Util.SETab);
    }

    /**
     * Update facing when the block is placed!
     * Don't forget to write super()!!
     */
    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (world.isRemote)
            return;
        
        TileEntity te = world.getTileEntity(x, y, z);

        if (te instanceof INetworkEventHandler)
        	Util.updateNetworkFields(te);
    }

    @Override
	public void onPostBlockPlaced(World world, int x, int y, int z,int meta) {
    	Util.scheduleBlockUpdate(world.getTileEntity(x, y, z));
	}
    
    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        dropItems(world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }

    /**
     * If this block has its own ItemBlock, just override this method and shouldRegister(set to false).
     *
     * @param name name of this block.
     *
     * @see simElectricity.Common.Blocks.BlockWire
     * @see simElectricity.Common.Items.ItemBlocks.ItemBlockWire
     */
    @Override
    public Block setBlockName(String name) {
        if (shouldRegister())
            GameRegistry.registerBlock(this, ItemBlockSE.class, name);
        return super.setBlockName(name);
    }

    public boolean registerInCreativeTab() {
        return true;
    }

    public boolean shouldRegister() {
        return true;
    }

    /**
     * Drop items inside the inventory
     */
    public static void dropItems(World world, int x, int y, int z) {
        Random rand = new Random();

        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (!(tileEntity instanceof IInventory)) {
            return;
        }
        IInventory inventory = (IInventory) tileEntity;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack item = inventory.getStackInSlot(i);

            if (item != null && item.stackSize > 0) {
                float rx = rand.nextFloat() * 0.8F + 0.1F;
                float ry = rand.nextFloat() * 0.8F + 0.1F;
                float rz = rand.nextFloat() * 0.8F + 0.1F;

                EntityItem entityItem = new EntityItem(world,
                        x + rx, y + ry, z + rz,
                        new ItemStack(item.getItem(), item.stackSize, item.getItemDamage()));

                if (item.hasTagCompound()) {
                    entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
                }

                float factor = 0.05F;
                entityItem.motionX = rand.nextGaussian() * factor;
                entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
                entityItem.motionZ = rand.nextGaussian() * factor;
                world.spawnEntityInWorld(entityItem);
                item.stackSize = 0;
            }
        }
    }
}
