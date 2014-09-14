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
import simElectricity.API.Energy;

public class TileSolarPanel extends TileSidedGenerator {
    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote)
            return;

        //Server only

        if (!worldObj.provider.isSurfaceWorld()){
        	checkAndSendChange(10, 100);
        	return;
        }
        
        if (worldObj.isDaytime()) {
            if (worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord))
                checkAndSendChange(18, 0.8F);
            else
                checkAndSendChange(0, Float.MAX_VALUE);
        } else {
            checkAndSendChange(12, 20);
        }
    }

    void checkAndSendChange(float voltage, float resistance) {
        if (voltage != outputVoltage | resistance != outputResistance) {
            outputVoltage = voltage;
            outputResistance = resistance;
            Energy.postTileChangeEvent(this);
        }
    }

    @Override
    public boolean canSetFunctionalSide(ForgeDirection newFunctionalSide) {
        return newFunctionalSide != ForgeDirection.UP;
    }
}
