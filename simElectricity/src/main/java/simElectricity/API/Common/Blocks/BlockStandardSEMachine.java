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

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import simElectricity.API.Common.TileStandardSEMachine;
import simElectricity.API.Util;

/**
 * Standard SE machine block
 *
 * @author <Meow J>
 */
public abstract class BlockStandardSEMachine extends BlockSidedFacingMachine {

    public BlockStandardSEMachine(Material material) {
        super(material);
    }

    public BlockStandardSEMachine() {
        this(Material.iron);
    }

    /**
     * If this machine only has horizontal facing, override this method and set to true.
     */
    public boolean ignoreVerticalFacing() {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        super.onBlockPlacedBy(world, pos, state, player, itemStack);
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(pos);

        if (!(te instanceof TileStandardSEMachine))
            return;

        EnumFacing functionalSide = Util.getPlayerSight(player, ignoreVerticalFacing());
        ((TileStandardSEMachine) te).setFacing(functionalSide.getOpposite());

        functionalSide = AutoFacing.autoConnect(te, functionalSide);
        ((TileStandardSEMachine) te).setFunctionalSide(functionalSide);
    }
}
