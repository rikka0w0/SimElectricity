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

package simElectricity.API.Common;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import simElectricity.API.ISidedFacing;

public abstract class TileSidedFacingMachine extends TileInventoryMachine implements ISidedFacing {
    public EnumFacing facing = EnumFacing.NORTH;

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        facing = EnumFacing.getFront(tagCompound.getByte("facing"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setByte("facing", (byte) facing.ordinal());
    }

    @Override
    public EnumFacing getFacing() {
        return facing;
    }

    //ISidedFacing
    @Override
    public void setFacing(EnumFacing newFacing) {
        facing = newFacing;
    }

    @Override
    public boolean canSetFacing(EnumFacing newFacing) {
        return true;
    }
}
