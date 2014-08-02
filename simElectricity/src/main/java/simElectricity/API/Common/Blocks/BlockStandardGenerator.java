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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Util;
import simElectricity.API.Common.TileSidedGenerator;

/**
 * Standard generator block
 *
 * @author <Meow J>
 */
public abstract class BlockStandardGenerator extends BlockContainerSE {
    public BlockStandardGenerator(Material material) {
        super(material);
    }

    public BlockStandardGenerator() {
        this(Material.iron);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        if (world.isRemote)
        	return;
    	
    	TileEntity te = world.getTileEntity(x, y, z);

        if (!(te instanceof TileSidedGenerator))
            return;

        ForgeDirection functionalSide = AutoFacing.autoConnect(te, Util.getPlayerSight(player).getOpposite());

        ((TileSidedGenerator) te).setFunctionalSide(functionalSide);
    }
}
