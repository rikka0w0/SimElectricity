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
import simElectricity.API.SEEnergy;
import simElectricity.API.IEnergyNetUpdateHandler;
import simElectricity.API.INetworkEventHandler;
import simElectricity.API.SEAPI;

import java.util.List;

public class TileIncandescentLamp extends TileStandardSEMachine implements IEnergyNetUpdateHandler, INetworkEventHandler {
    public int lightLevel = 0;
    
    @Override
    public boolean canSetFunctionalSide(ForgeDirection newFunctionalSide) {
        //FunctionalSide Facing
        return true;
    }

    @Override
    public double getOutputVoltage() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 9900; // 5 watt at 220V
    }

    @Override
	public void onOverVoltage(){
    	//worldObj.createExplosion(null, xCoord, yCoord, zCoord, (float) (4F + SEEnergy.getVoltage(this) / 265), true);
    }
    
    @Override
    public void onEnergyNetUpdate() {
    	double voltage = SEEnergy.getVoltage(this);
        lightLevel = (int) (voltage*voltage/getResistance() / 0.3F);
        if (lightLevel > 15)
            lightLevel = 15;
        
        SEAPI.networkManager.updateNetworkFields(this);

        checkVoltage(SEEnergy.getVoltage(this), 265);
    }

    @Override
    public void addNetworkFields(List fields) {
        fields.add("lightLevel");
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void onFieldUpdate(String[] fields, Object[] values) {
    }
}
