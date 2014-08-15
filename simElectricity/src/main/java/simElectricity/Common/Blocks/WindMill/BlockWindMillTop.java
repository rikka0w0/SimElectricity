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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.Blocks.BlockContainerSE;
import simElectricity.API.ISidedFacing;
import simElectricity.API.Network;
import simElectricity.API.Util;
import simElectricity.Common.Core.SEItems;

public class BlockWindMillTop extends BlockContainerSE {
    private IIcon[] iconBuffer = new IIcon[6];

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i1, float f1, float f2, float f3) {
        TileWindMillTop te = (TileWindMillTop) world.getTileEntity(x, y, z);
        ItemStack playerItem = player.getCurrentEquippedItem();

        if (player.isSneaking())
            return false;


        if (te.settled) {
            if (playerItem != null)
                return false;

            dropBlockAsItem(world, x, y + 1, z, new ItemStack(SEItems.fan, 1));

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
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (te instanceof TileWindMillTop) {
            if (((TileWindMillTop) te).settled)
                dropBlockAsItem(world, x, y, z, new ItemStack(SEItems.fan, 1));
        }

        super.breakBlock(world, x, y, z, block, meta);
    }

    public BlockWindMillTop() {
        super();
        setBlockName("WindMillTop");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
        iconBuffer[0] = r.registerIcon("simElectricity:WindMill_Side");
        iconBuffer[1] = r.registerIcon("simElectricity:WindMill_Side");
        iconBuffer[2] = r.registerIcon("simElectricity:WindMill_Front");
        iconBuffer[3] = r.registerIcon("simElectricity:WindMill_Back");
        iconBuffer[4] = r.registerIcon("simElectricity:WindMill_Side");
        iconBuffer[5] = r.registerIcon("simElectricity:WindMill_Side");
    }


    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);

        return iconBuffer[Util.getTextureOnSide(side, ((ISidedFacing) te).getFacing())];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return iconBuffer[Util.getTextureOnSide(side, ForgeDirection.WEST)];
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        TileEntity te = world.getTileEntity(x, y, z);

        ((ISidedFacing) te).setFacing(Util.getPlayerSight(player, true).getOpposite());
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileWindMillTop();
    }
}
