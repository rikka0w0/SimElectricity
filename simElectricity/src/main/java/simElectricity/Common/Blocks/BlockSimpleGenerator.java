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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.Blocks.BlockStandardSEMachine;
import simElectricity.API.ISidedFacing;
import simElectricity.API.Util;
import simElectricity.Common.Blocks.TileEntity.TileSimpleGenerator;
import simElectricity.SimElectricity;

import java.util.Random;

public class BlockSimpleGenerator extends BlockStandardSEMachine {
    private IIcon[] iconBuffer = new IIcon[7];

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileSimpleGenerator))
            return;

        if (((TileSimpleGenerator) te).isWorking) {
            double d0 = (x);
            double d1 = (y);
            double d2 = (z);
            double d3 = 0.2199999988079071D;
            double d4 = 0.27000001072883606D;
            world.spawnParticle("smoke", d0 + d4 + 0.25F, d1 + d3 + 1F, d2 + 0.5F, 0.0D, 0.0D, 0.0D);
            world.spawnParticle("smoke", d0 + d4 + 0.15F, d1 + d3 + 1F, d2 + 0.5F, 0.0D, 0.0D, 0.0D);
            world.spawnParticle("smoke", d0 + d4 + 0.4F, d1 + d3 + 1F, d2 + 0.6F, 0.0D, 0.0D, 0.0D);
            switch (((TileSimpleGenerator) te).getFacing()) {
                case WEST:
                    d0 -= 0.4F;
                    d1 += 0.1F;
                    d2 += 0.5F;
                    break;
                case SOUTH:
                    d0 += 0.25F;
                    d1 += 0.1F;
                    d2 += 1.1F;
                    break;
                case NORTH:
                    d0 += 0.25F;
                    d1 += 0.1F;
                    d2 -= 0.1F;
                    break;
                case EAST:
                    d0 += 0.8F;
                    d1 += 0.1F;
                    d2 += 0.5F;
                    break;
                default:
                    break;
            }

            world.spawnParticle("smoke", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
            world.spawnParticle("flame", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i1, float f1, float f2, float f3) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (player.isSneaking())
            return false;

        if (!(te instanceof TileSimpleGenerator))
            return false;

        player.openGui(SimElectricity.instance, 0, world, x, y, z);
        return true;
    }

    public BlockSimpleGenerator() {
        super();
        setHardness(2.0F);
        setResistance(5.0F);
        setBlockName("SimpleGenerator");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
        iconBuffer[0] = r.registerIcon("simElectricity:SimpleGenerator_Bottom");
        iconBuffer[1] = r.registerIcon("simElectricity:SimpleGenerator_Top");
        iconBuffer[2] = r.registerIcon("simElectricity:SimpleGenerator_Front");
        iconBuffer[3] = r.registerIcon("simElectricity:SimpleGenerator_Side");
        iconBuffer[4] = r.registerIcon("simElectricity:SimpleGenerator_Side");
        iconBuffer[5] = r.registerIcon("simElectricity:SimpleGenerator_Side");
        iconBuffer[6] = r.registerIcon("simElectricity:SimpleGenerator_Front_W");
    }


    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (!(te instanceof ISidedFacing))
            return iconBuffer[0];

        int iconIndex = Util.getTextureOnSide(side, ((ISidedFacing) te).getFacing());
        if (((TileSimpleGenerator) te).isWorking && iconIndex == 2)
            iconIndex = 6;

        return iconBuffer[iconIndex];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return iconBuffer[Util.getTextureOnSide(side, ForgeDirection.WEST)];
    }


    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        world.markBlockForUpdate(x, y, z);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileSimpleGenerator();
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileSimpleGenerator))
            return 0;

        return ((TileSimpleGenerator) te).isWorking ? 13 : 0;
    }
}
