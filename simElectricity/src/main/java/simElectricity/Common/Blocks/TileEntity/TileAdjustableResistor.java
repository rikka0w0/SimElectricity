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
import simElectricity.API.Common.TileStandardSEMachine;
import simElectricity.API.SEEnergy;
import simElectricity.API.INetworkEventHandler;

import java.util.List;

public class TileAdjustableResistor extends TileStandardSEMachine implements INetworkEventHandler {
    public float resistance = 1000;
    public float powerConsumed = 0;
    public float power = 0;

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote)
            return;

        double voltage = SEEnergy.getVoltage(this);
        power = (float) (voltage*voltage/resistance);
        powerConsumed += power / 20F;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        resistance = tagCompound.getFloat("resistance");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setFloat("resistance", resistance);
    }

    @Override
    public void onFieldUpdate(String[] fields, Object[] values) {
        //Handling on server side
        if (!worldObj.isRemote) {
            for (String s : fields)
                if (s.contains("resistance"))
                    SEEnergy.postTileChangeEvent(this);
        }
    }

    @Override
    public double getResistance() {
        return resistance;
    }
    
    @Override
    public void addNetworkFields(List fields) {

    }
}
