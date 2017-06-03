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

package simElectricity.Templates.TileEntity;

import simElectricity.API.IEnergyNetUpdateHandler;
import simElectricity.API.SEAPI;
import simElectricity.Templates.Common.TileStandardSEMachine;
import simElectricity.Templates.Utils.IGuiSyncHandler;

public class TileVoltageMeter extends TileStandardSEMachine implements IEnergyNetUpdateHandler, IGuiSyncHandler{
    @Override
    public double getResistance() {
        return 1e6F;
    }

    @Override
    public double getOutputVoltage() {
        return 0;
    }

	@Override
	public void onEnergyNetUpdate() {
		SEAPI.energyNetAgent.getVoltage(tile);
	}

	@Override
	public void onGuiEvent(byte eventID, Object[] data) {
		
	}
}
