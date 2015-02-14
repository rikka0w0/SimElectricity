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

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import simElectricity.API.EnergyTile.IConductor;

import java.util.Arrays;


public class AutoFacing {


    public static final EnumFacing[] VALID_DIRECTIONS = {EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST};

    /**
     * @param tileEntity       tileEntity
     * @param defaultDirection default direction
     * @return valid conductor direction. If there is no conductor nearby, return default direction.
     */
    public static EnumFacing autoConnect(TileEntity tileEntity, EnumFacing defaultDirection) {
        return autoConnect(tileEntity, defaultDirection, new EnumFacing[]{});
    }

    /**
     * Exception version of auto-facing
     *
     * @param tileEntity       tileEntity
     * @param defaultDirection default direction
     * @param exception        exception direction
     * @return valid conductor direction. If there is no conductor nearby, return default direction
     * @see simElectricity.Common.Blocks.BlockSwitch
     */
    public static EnumFacing autoConnect(TileEntity tileEntity, EnumFacing defaultDirection, EnumFacing exception) {
        return autoConnect(tileEntity, defaultDirection, new EnumFacing[]{exception});
    }

    /**
     * Exceptions array version of auto-facing
     *
     * @param tileEntity       tileEntity
     * @param defaultDirection default direction
     * @param exceptions       exception directions array
     * @return valid conductor direction. If there is no conductor nearby, return default direction.
     */
    public static EnumFacing autoConnect(TileEntity tileEntity, EnumFacing defaultDirection, EnumFacing[] exceptions) {
        for (EnumFacing direction : VALID_DIRECTIONS) {
            if (tileEntity.getWorld().getTileEntity(new BlockPos(tileEntity.getPos().getX() + direction.getFrontOffsetX(), tileEntity.getPos().getY() + direction.getFrontOffsetY(), tileEntity.getPos().getZ() + direction.getFrontOffsetZ())) instanceof IConductor
                    && !Arrays.asList(exceptions).contains(direction))
                return direction;
        }
        return defaultDirection;
    }
}
