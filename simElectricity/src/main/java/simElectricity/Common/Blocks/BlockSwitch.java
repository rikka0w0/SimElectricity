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

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import simElectricity.API.Common.Blocks.AutoFacing;
import simElectricity.API.Common.Blocks.BlockContainerSE;
import simElectricity.API.Energy;
import simElectricity.API.Network;
import simElectricity.API.Util;
import simElectricity.Common.Blocks.TileEntity.TileSwitch;
import simElectricity.SimElectricity;

public class BlockSwitch extends BlockContainerSE {

    public BlockSwitch() {
        super();
        setUnlocalizedName("switch");
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking())
            return false;

        TileSwitch te = (TileSwitch) world.getTileEntity(pos);

        if (te.getFacing() != side) {
            player.openGui(SimElectricity.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        } else {
            if (!world.isRemote) {
                te.isOn = !te.isOn;
                Network.updateTileEntityFields(te, "isOn");
                Energy.postTileRejoinEvent(te);
            }
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        TileSwitch te = (TileSwitch) world.getTileEntity(pos);

        te.setFacing(Util.getPlayerSight(player, false).getOpposite());

        te.inputSide = AutoFacing.autoConnect(te, EnumFacing.UP, te.getFacing());
        te.outputSide = AutoFacing.autoConnect(te, te.inputSide.getOpposite(), new EnumFacing[]{te.getFacing(), te.inputSide});

        if (te.outputSide == te.getFacing())
            te.outputSide = te.outputSide.rotateY();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileSwitch();
    }
}
