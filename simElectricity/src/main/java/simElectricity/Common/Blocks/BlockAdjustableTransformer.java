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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import simElectricity.API.SEAPI;
import simElectricity.API.Common.Blocks.BlockContainerSE;
import simElectricity.Common.Blocks.TileEntity.TileAdjustableTransformer;
import simElectricity.SimElectricity;

public class BlockAdjustableTransformer extends BlockContainerSE {
    private IIcon[] iconBuffer = new IIcon[3];

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i1, float f1, float f2, float f3) {
        if (player.isSneaking())
            return false;

        player.openGui(SimElectricity.instance, 0, world, x, y, z);
        return true;
    }

    public BlockAdjustableTransformer() {
        super();
        setBlockName("AdjustableTransformer");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
        iconBuffer[0] = r.registerIcon("simElectricity:Transformer_Side");
        iconBuffer[1] = r.registerIcon("simElectricity:Transformer_Secondary");
        iconBuffer[2] = r.registerIcon("simElectricity:Transformer_Primary");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileAdjustableTransformer te = (TileAdjustableTransformer) world.getTileEntity(x, y, z);


        if (side == te.primarySide.ordinal())
            return iconBuffer[2];
        else if (side == te.secondarySide.ordinal())
            return iconBuffer[1];
        else
            return iconBuffer[0];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 4)
            return iconBuffer[1];
        else
            return iconBuffer[0];
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileAdjustableTransformer))
            return;

        ((TileAdjustableTransformer) te).secondarySide = SEAPI.utils.getPlayerSight(player, false);
        ((TileAdjustableTransformer) te).primarySide = ((TileAdjustableTransformer) te).secondarySide.getOpposite();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileAdjustableTransformer();
    }
}
