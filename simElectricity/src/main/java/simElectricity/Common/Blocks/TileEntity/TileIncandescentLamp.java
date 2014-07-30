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

import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.TileStandardSEMachine;
import simElectricity.API.Energy;
import simElectricity.API.IEnergyNetUpdateHandler;
import simElectricity.API.IUpdateOnWatch;
import simElectricity.API.Util;

public class TileIncandescentLamp extends TileStandardSEMachine implements IEnergyNetUpdateHandler, IUpdateOnWatch {
    public int lightLevel = 0;

    @Override
    public boolean canSetFunctionalSide(ForgeDirection newFunctionalSide) {
        return newFunctionalSide != ForgeDirection.UP;
    }

    @Override
    public float getOutputVoltage() {
        return 0;
    }

    @Override
    public float getResistance() {
        return 9900; // 5 watt at 220V
    }

    @Override
    public int getInventorySize() {
        return 0;
    }

    @Override
    public void onEnergyNetUpdate() {
        lightLevel = (int) (Energy.getPower(this) / 0.3F);
        if (lightLevel > 15)
            lightLevel = 15;
        if (Energy.getVoltage(this) > 265)
            worldObj.createExplosion(null, xCoord, yCoord, zCoord, 4F + Energy.getVoltage(this) / 265, true);

        onWatch();
    }

    @Override
    public void onWatch() {
        Util.updateTileEntityField(this, "lightLevel");
        Util.scheduleBlockUpdate(this);
    }
}
