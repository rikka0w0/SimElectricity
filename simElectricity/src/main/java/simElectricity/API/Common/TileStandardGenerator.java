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
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Energy;
import simElectricity.API.EnergyTile.IEnergyTile;
import simElectricity.API.Util;

public abstract class TileStandardGenerator extends TileEntity implements IEnergyTile {
    private ForgeDirection functionalSide = ForgeDirection.NORTH;
    private boolean isAddedToEnergyNet = false;

    public float outputVoltage = 12;
    public float outputResistance = 0.01F;

    public void init() {
    }

    // TileEntity
    @Override
    public void updateEntity() {
        if (!worldObj.isRemote && !isAddedToEnergyNet) {
            Energy.postTileAttachEvent(this);
            this.isAddedToEnergyNet = true;
            init();
            Util.scheduleBlockUpdate(this);
        }
    }

    @Override
    public void invalidate() {
        if (!worldObj.isRemote & isAddedToEnergyNet) {
            Energy.postTileDetachEvent(this);
            this.isAddedToEnergyNet = false;
        }
        super.invalidate();
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        functionalSide = ForgeDirection.getOrientation(tagCompound.getByte("functionalSide"));
        outputVoltage = tagCompound.getFloat("outputVoltage");
        outputResistance = tagCompound.getFloat("outputResistance");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setByte("functionalSide", (byte) functionalSide.ordinal());
        tagCompound.setFloat("outputVoltage", outputVoltage);
        tagCompound.setFloat("outputResistance", outputResistance);
    }


    // IEnergyTile
    @Override
    public float getResistance() {
        return outputResistance;
    }

    @Override
    public float getOutputVoltage() {
        return outputVoltage;
    }

    @Override
    public ForgeDirection getFunctionalSide() {
        return functionalSide;
    }

    @Override
    public void setFunctionalSide(ForgeDirection newFunctionalSide) {
        functionalSide = newFunctionalSide;
    }

    @Override
    public boolean canSetFunctionalSide(ForgeDirection newFunctionalSide) {
        return true;
    }
}
