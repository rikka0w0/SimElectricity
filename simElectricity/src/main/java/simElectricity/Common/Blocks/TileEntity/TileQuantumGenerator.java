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
import simElectricity.API.SEAPI;
import simElectricity.API.INetworkEventHandler;

import java.util.List;

public class TileQuantumGenerator extends TileSidedGenerator implements INetworkEventHandler {

    @Override
    public void onLoad() {
        if (this.outputResistance == Double.MAX_VALUE) {
            outputResistance = 0.001;
            outputVoltage = 230;
        }
    }

    @Override
    public void onFieldUpdate(String[] fields, Object[] values) {
        //Handling on server side
        if (!worldObj.isRemote) {
            for (String s : fields) {
                if (s.contains("outputVoltage") || s.contains("outputResistance")) {
                	SEAPI.energyNetAgent.markTileForUpdate(this);
                }
            }
        }
    }

    @Override
    public boolean canSetFunctionalSide(ForgeDirection newFunctionalSide) {
        return true;
    }
    
    @Override
    public void addNetworkFields(List fields) {
    }

    @Override
    public boolean canSetFacing(ForgeDirection newFacing) {
        return newFacing != ForgeDirection.UP && newFacing != ForgeDirection.DOWN;
    }
}
