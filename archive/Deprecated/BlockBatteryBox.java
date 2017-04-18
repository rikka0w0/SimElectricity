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
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.SEAPI;
import simElectricity.Templates.Common.BlockStandardGenerator;
import simElectricity.Templates.TileEntity.TileBatteryBox;
import simElectricity.Templates.Utils.ClientUtils;

public class BlockBatteryBox extends BlockStandardGenerator {
    private IIcon[] iconBuffer = new IIcon[6];

    public BlockBatteryBox() {
        super();
        setBlockName("BatteryBox");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
        iconBuffer[0] = r.registerIcon("simElectricity:SolarPanel_Bottom");
        iconBuffer[1] = r.registerIcon("simElectricity:SolarPanel_Bottom");
        iconBuffer[2] = r.registerIcon("simElectricity:SolarPanel_Front");
        iconBuffer[3] = r.registerIcon("simElectricity:SolarPanel_Side");
        iconBuffer[4] = r.registerIcon("simElectricity:SolarPanel_Side");
        iconBuffer[5] = r.registerIcon("simElectricity:SolarPanel_Side");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (!(te instanceof TileBatteryBox))
            return iconBuffer[0];

        int iconIndex = ClientUtils.getTextureID(side, ((TileBatteryBox) te).getFunctionalSide());

        if (((TileBatteryBox) te).getFunctionalSide() == ForgeDirection.DOWN) {
            if (iconIndex == 3) {
                iconIndex = 1;
            } else if (iconIndex == 1) {
                iconIndex = 3;
            }
        }

        return iconBuffer[iconIndex];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return iconBuffer[ClientUtils.getTextureID(side, ForgeDirection.WEST)];
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileBatteryBox();
    }


}
