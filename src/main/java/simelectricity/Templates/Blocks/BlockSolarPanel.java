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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.Templates.Common.BlockStandardGenerator;
import simelectricity.Templates.Common.TileSidedGenerator;
import simelectricity.Templates.TileEntity.TileSolarPanel;
import simelectricity.Templates.Utils.Utils;

public class BlockSolarPanel extends BlockStandardGenerator {
    private IIcon[] iconBuffer = new IIcon[4];

    public BlockSolarPanel() {
        super();
        setBlockName("SolarPanel");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileSolarPanel();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(x, y, z);

        if (!(te instanceof TileSidedGenerator))
            return;

        ForgeDirection functionalSide = Utils.autoConnect(te, Utils.getPlayerSight(player, false).getOpposite(), ForgeDirection.UP);
        if (functionalSide == ForgeDirection.UP)
            functionalSide = ForgeDirection.DOWN;

        ((TileSidedGenerator) te).functionalSide = functionalSide;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
        iconBuffer[0] = r.registerIcon("simElectricity:SolarPanel_Bottom");
        iconBuffer[1] = r.registerIcon("simElectricity:SolarPanel_Top");
        iconBuffer[2] = r.registerIcon("simElectricity:SolarPanel_Front");
        iconBuffer[3] = r.registerIcon("simElectricity:SolarPanel_Side");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (!(te instanceof TileSolarPanel))
            return iconBuffer[0];

        if (((TileSolarPanel) te).functionalSide.ordinal() == side) {
            return iconBuffer[2];
        } else if (side == 0) { //Down
            return iconBuffer[0];
        } else if (side == 1) { //Up
            return iconBuffer[1];
        }

        return iconBuffer[3];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 4)
            return iconBuffer[2];
        else if (side == 1) //Up
            return iconBuffer[1];
        else
            return iconBuffer[3];
    }
}
