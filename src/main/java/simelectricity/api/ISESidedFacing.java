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

package simelectricity.api;

import net.minecraft.util.EnumFacing;

/**
 * For TileEntities only, implementing this allows glove item to change the facing, all machines are recommended to implement this!
 */
public interface ISESidedFacing {
    EnumFacing getFacing();

    /**
     * Called when the facing is about to be set
     * Note: Called from SERVER ONLY! Initiate a server->client sync if needed
     */
    void setFacing(EnumFacing newFacing);

    /**
     * return true only for valid new facing, return false otherwise
     */
    boolean canSetFacing(EnumFacing newFacing);
}
