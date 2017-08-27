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
 * A tile entity should implement this, if its facing want to be changed by the glove item, all machines are recommended to implement this!
 */
public interface ISidedFacing {
    /**
     * Return the facing(Should have a line of "{@code private EnumFacing facing = EnumFacing.NORTH;} in the tile entity class)
     */
    EnumFacing getFacing();

    /**
     * Usually contains a line of
     * <p/>
     * {@code facing = newFacing;}
     * </p>
     * Note: SERVER ONLY! Initiate a server->client sync if needed
     */
    void setFacing(EnumFacing newFacing);

    /**
     * Tell the glove, which facing is allowed or not allowed, directly return false means glove can do nothing!
     */
    boolean canSetFacing(EnumFacing newFacing);
}
