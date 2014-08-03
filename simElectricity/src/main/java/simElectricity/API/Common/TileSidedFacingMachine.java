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
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.INetworkEventHandler;
import simElectricity.API.ISidedFacing;

import java.util.List;

public abstract class TileSidedFacingMachine extends TileInventoryMachine implements ISidedFacing, INetworkEventHandler {
    public ForgeDirection facing = ForgeDirection.NORTH;

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        facing = ForgeDirection.getOrientation((tagCompound.getByte("facing")));
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setByte("facing", (byte) facing.ordinal());
    }

    //ISidedFacing
    @Override
    public void setFacing(ForgeDirection newFacing) {
        facing = newFacing;
    }

    @Override
    public ForgeDirection getFacing() {
        return facing;
    }

    @Override
    public boolean canSetFacing(ForgeDirection newFacing) {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addNetworkFields(List fields) {
        fields.add("facing");
    }

    @Override
    public void onFieldUpdate(String[] fields, Object[] values, boolean isClient) {
        if (isClient) {
            for (String s : fields) {
                if (s.contains("facing")) {
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                }
            }
        }
    }
}
