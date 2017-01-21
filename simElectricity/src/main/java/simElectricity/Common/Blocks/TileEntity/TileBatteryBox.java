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
import simElectricity.API.Common.TileSidedGenerator;
import simElectricity.API.SEEnergy;
import simElectricity.Common.SEUtils;

public class TileBatteryBox extends TileSidedGenerator {

    private float chargingVoltage = 115;
    private float dischargeVoltage = 110;

    private float wattTickStore = 0;
    private float wattTickStoreMax = 1000000;

    @Override
    public void updateEntity() {
        super.updateEntity();

        //Server only
        if (worldObj.isRemote)
            return;

        if ((SEEnergy.getVoltage(this) >= chargingVoltage) && (wattTickStore < wattTickStoreMax)) {
            checkAndSendChange(0, 100F);
            //wattTickStore += (SEEnergy.getPower(this) * 0.05);

            SEUtils.logInfo("charging, wattTickStore: " + wattTickStore);
        } else if ((SEEnergy.getVoltage(this) <= (dischargeVoltage + 0.1F)) && (wattTickStore > 0)) {
            checkAndSendChange(dischargeVoltage, 0.8F);
            //wattTickStore -= SEEnergy.getWorkDonePerTick(this);

            SEUtils.logInfo("discharge, wattTickStore: " + wattTickStore);
        } else {
            checkAndSendChange(0, Float.MAX_VALUE);
        }

    }

    void checkAndSendChange(float voltage, float resistance) {
        if (voltage != outputVoltage | resistance != outputResistance) {
            outputVoltage = voltage;
            outputResistance = resistance;
            SEEnergy.postTileChangeEvent(this);
        }
    }

    @Override
    public int getInventorySize() {
        return 0;
    }

    @Override
    public boolean canSetFunctionalSide(ForgeDirection newFunctionalSide) {
        // FunctionalSide Facing
        return true;
    }
}
