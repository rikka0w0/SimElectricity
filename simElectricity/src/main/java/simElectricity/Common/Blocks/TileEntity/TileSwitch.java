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

package simElectricity.Common.Blocks.TileEntity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import simElectricity.API.Common.TileEntitySE;
import simElectricity.API.*;
import simElectricity.API.EnergyTile.IBaseComponent;
import simElectricity.API.EnergyTile.IConductor;
import simElectricity.API.EnergyTile.IConnectable;
import simElectricity.API.EnergyTile.IManualJunction;

import java.util.List;

public class TileSwitch extends TileEntitySE implements IManualJunction, IConnectable, ISidedFacing, IEnergyNetUpdateHandler, INetworkEventHandler {
    public double current = 0F;

    public EnumFacing inputSide = EnumFacing.NORTH, outputSide = EnumFacing.SOUTH, facing = EnumFacing.WEST;
    public float resistance = 0.005F;
    public float maxCurrent = 1F;
    public boolean isOn = false;

    @Override
    public boolean attachToEnergyNet() {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        resistance = tagCompound.getFloat("resistance");
        maxCurrent = tagCompound.getFloat("maxCurrent");
        isOn = tagCompound.getBoolean("isOn");
        inputSide = EnumFacing.getFront(tagCompound.getByte("inputSide"));
        outputSide = EnumFacing.getFront(tagCompound.getByte("outputSide"));
        facing = EnumFacing.getFront(tagCompound.getByte("facing"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setFloat("resistance", resistance);
        tagCompound.setFloat("maxCurrent", maxCurrent);
        tagCompound.setBoolean("isOn", isOn);
        tagCompound.setByte("inputSide", (byte) inputSide.ordinal());
        tagCompound.setByte("outputSide", (byte) outputSide.ordinal());
        tagCompound.setByte("facing", (byte) facing.ordinal());
    }

    @Override
    public void addNetworkFields(List fields) {

    }

    @Override
    public void onFieldUpdate(String[] fields, Object[] values) {
        if (worldObj.isRemote) {
            worldObj.markBlockForUpdate(pos);
        } else {//Handling on server side
            for (String s : fields) {
                if (s.contains("inputSide") || s.contains("outputSide") || s.contains("isOn")) {
                    Energy.postTileRejoinEvent(this);
                    worldObj.notifyBlockOfStateChange(pos, blockType);
                } else if (s.contains("resistance")) {
                    Energy.postTileChangeEvent(this);
                } else if (s.contains("maxCurrent")) {
                    onEnergyNetUpdate();
                }
            }
        }
    }

    @Override
    public double getResistance() {
        return resistance / 2;
    }

    @Override
    public boolean canConnectOnSide(EnumFacing side) {
        return side == inputSide || side == outputSide;
    }

    @Override
    public void addNeighbors(List<IBaseComponent> list) {
        if (isOn) {
            TileEntity neighbor = Util.getTileEntityonDirection(this, inputSide);

            if (neighbor instanceof IConductor)
                list.add((IConductor) neighbor);

            neighbor = Util.getTileEntityonDirection(this, outputSide);

            if (neighbor instanceof IConductor)
                list.add((IConductor) neighbor);
        }
    }

    @Override
    public EnumFacing getFacing() {
        return facing;
    }

    @Override
    public void setFacing(EnumFacing newFacing) {
        facing = newFacing;
    }

    @Override
    public boolean canSetFacing(EnumFacing newFacing) {
        return newFacing != inputSide && newFacing != outputSide;
    }

    @Override
    public void onEnergyNetUpdate() {
        current = getCurrent();
        if (current > maxCurrent) {
            isOn = false;
            Energy.postTileRejoinEvent(this);
            Network.updateTileEntityFields(this, "isOn");
        }
    }

    private double getCurrent() {
        if (!isOn)
            return 0;

        TileEntity neighbor;
        for (EnumFacing dir : new EnumFacing[]{inputSide, outputSide}) {
            neighbor = Util.getTileEntityonDirection(this, dir);
            if (neighbor instanceof IConductor) {
                return Math.abs((Energy.getVoltage((IConductor) neighbor) - (Energy.getVoltage(this))) /
                        (((IConductor) neighbor).getResistance() + this.getResistance()));
            }
        }
        return 0;
    }

    @Override
    public double getResistance(IBaseComponent neighbor) {
        return 0;
    }
}
