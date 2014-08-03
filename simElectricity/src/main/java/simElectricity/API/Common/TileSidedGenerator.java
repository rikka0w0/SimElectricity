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

public abstract class TileSidedGenerator extends TileStandardSEMachine {
    public float outputVoltage = 0;
    public float outputResistance = Float.MAX_VALUE;

    @Override
    public float getResistance() {
        return outputResistance;
    }

    @Override
    public float getOutputVoltage() {
        return outputVoltage;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        outputVoltage = tagCompound.getFloat("outputVoltage");
        outputResistance = tagCompound.getFloat("outputResistance");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setFloat("outputResistance", outputResistance);
        tagCompound.setFloat("outputVoltage", outputVoltage);
    }
}
