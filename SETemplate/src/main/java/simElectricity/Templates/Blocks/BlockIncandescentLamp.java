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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import simElectricity.Templates.Common.BlockStandardSEMachine;
import simElectricity.Templates.TileEntity.TileIncandescentLamp;

public class BlockIncandescentLamp extends BlockStandardSEMachine {
    private IIcon[] iconBuffer = new IIcon[4];

    public BlockIncandescentLamp() {
        super();
        setBlockName("IncandescentLamp");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
        iconBuffer[0] = r.registerIcon("simElectricity:IncandescentLamp_Side_Off");
        iconBuffer[1] = r.registerIcon("simElectricity:IncandescentLamp_Side_On");
        iconBuffer[2] = r.registerIcon("simElectricity:IncandescentLamp_Front_Off");
        iconBuffer[3] = r.registerIcon("simElectricity:IncandescentLamp_Front_On");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (!(te instanceof TileIncandescentLamp))
            return iconBuffer[0];

        if (((TileIncandescentLamp) te).lightLevel>8){
        	if (((TileIncandescentLamp) te).getFunctionalSide().ordinal() == side)
        		return iconBuffer[3];
        	else
        		return iconBuffer[1];
        }else{
        	if (((TileIncandescentLamp) te).getFunctionalSide().ordinal() == side)
        		return iconBuffer[2];
        	else
        		return iconBuffer[0];       	
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 4)
            return iconBuffer[2];
        else
            return iconBuffer[0];
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (!(te instanceof TileIncandescentLamp))
            return 0;

        return ((TileIncandescentLamp) te).lightLevel;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileIncandescentLamp();
    }
}
