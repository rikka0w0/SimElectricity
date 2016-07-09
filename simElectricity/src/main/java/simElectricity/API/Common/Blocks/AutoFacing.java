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
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.EnergyTile.ISEConductor;

import java.util.Arrays;

public class AutoFacing {

    /**
     * @param tileEntity       tileEntity
     * @param defaultDirection default direction
     *
     * @return valid conductor direction. If there is no conductor nearby, return default direction.
     */
    public static ForgeDirection autoConnect(TileEntity tileEntity, ForgeDirection defaultDirection) {
        return autoConnect(tileEntity, defaultDirection, new ForgeDirection[] { });
    }

    /**
     * Exception version of auto-facing
     *
     * @param tileEntity       tileEntity
     * @param defaultDirection default direction
     * @param exception        exception direction
     *
     * @return valid conductor direction. If there is no conductor nearby, return default direction
     *
     * @see simElectricity.Common.Blocks.BlockSwitch
     */
    public static ForgeDirection autoConnect(TileEntity tileEntity, ForgeDirection defaultDirection, ForgeDirection exception) {
        return autoConnect(tileEntity, defaultDirection, new ForgeDirection[] { exception });
    }

    /**
     * Exceptions array version of auto-facing
     *
     * @param tileEntity       tileEntity
     * @param defaultDirection default direction
     * @param exceptions       exception directions array
     *
     * @return valid conductor direction. If there is no conductor nearby, return default direction.
     */
    public static ForgeDirection autoConnect(TileEntity tileEntity, ForgeDirection defaultDirection, ForgeDirection[] exceptions) {
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if (tileEntity.getWorldObj().getTileEntity(tileEntity.xCoord + direction.offsetX,
                    tileEntity.yCoord + direction.offsetY,
                    tileEntity.zCoord + direction.offsetZ) instanceof ISEConductor
                    && !Arrays.asList(exceptions).contains(direction))
                return direction;
        }
        return defaultDirection;
    }
}
