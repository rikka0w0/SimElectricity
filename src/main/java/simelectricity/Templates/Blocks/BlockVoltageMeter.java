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

package simelectricity.Templates.Blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import simelectricity.Templates.Common.BlockStandardSEMachine;
import simelectricity.Templates.TileEntity.TileVoltageMeter;
import simelectricity.Templates.SETemplate;

public class BlockVoltageMeter extends BlockStandardSEMachine {
    private IIcon[] iconBuffer = new IIcon[3];

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i1, float f1, float f2, float f3) {
        if (player.isSneaking())
            return false;

        //if (world.isRemote)
        player.openGui(SETemplate.instance, 0, world, x, y, z);
        return true;
    }

    public BlockVoltageMeter() {
        super();
        setBlockName("VoltageMeter");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
        iconBuffer[0] = r.registerIcon("simElectricity:VoltageMeter_Side");
        iconBuffer[1] = r.registerIcon("simElectricity:VoltageMeter_Front");
        iconBuffer[2] = r.registerIcon("simElectricity:VoltageMeter_Back");
    }


    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (!(te instanceof TileVoltageMeter))
            return iconBuffer[0];

        if (((TileVoltageMeter) te).getFacing().ordinal() == side)
            return iconBuffer[1];
        else if (((TileVoltageMeter) te).getFunctionalSide().ordinal() == side)
            return iconBuffer[2];

        return iconBuffer[0];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 4)
            return iconBuffer[1];
        return iconBuffer[0];
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileVoltageMeter();
    }
}
