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

package simelectricity.Templates.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.api.SEAPI;
import simelectricity.Templates.Common.TileSidedGenerator;
import simelectricity.Templates.Utils.IGuiSyncHandler;

public class TileQuantumGenerator extends TileSidedGenerator implements IGuiSyncHandler {

    @Override
    public void onLoad() {
        if (this.outputResistance == Double.MAX_VALUE) {
            outputResistance = 0.001;
            outputVoltage = 230;
        }
    }

    @Override
    public boolean canSetFunctionalSide(ForgeDirection newFunctionalSide) {
        return true;
    }
    
    @Override
    public boolean canSetFacing(ForgeDirection newFacing) {
        return newFacing != ForgeDirection.UP && newFacing != ForgeDirection.DOWN;
    }
    
	/////////////////////////////////////////////////////////
	///IGuiSyncHandler
	/////////////////////////////////////////////////////////
	@Override
	public void onGuiEvent(byte eventID, Object[] data) {
		if (eventID != IGuiSyncHandler.EVENT_BUTTON_CLICK)
			return;
		
		boolean isCtrlDown = (Boolean) data[0];
		byte button = (Byte) data[1];
		
		double outputVoltage = this.outputVoltage;
		double outputResistance = this.outputResistance;
		
        switch (button) {
        case 0:
            if (isCtrlDown)
                outputVoltage -= 100;
            else
                outputVoltage -= 10;
            break;
        case 1:
            if (isCtrlDown)
                outputVoltage -= 0.1;
            else
                outputVoltage -= 1;
            break;
        case 2:
            if (isCtrlDown)
                outputVoltage += 0.1;
            else
                outputVoltage += 1;
            break;
        case 3:
            if (isCtrlDown)
                outputVoltage += 100;
            else
                outputVoltage += 10;
            break;
        case 4:
            if (isCtrlDown)
                outputResistance -= 1;
            else
                outputResistance -= 0.1;
            break;
        case 5:
            if (isCtrlDown)
                outputResistance -= 0.001;
            else
                outputResistance -= 0.01;
            break;
        case 6:
            if (isCtrlDown)
                outputResistance += 0.001;
            else
                outputResistance += 0.01;
            break;
        case 7:
            if (isCtrlDown)
                outputResistance += 1;
            else
                outputResistance += 0.1;
            break;
        default:
	    }
	
	    if (outputVoltage < 0)
	        outputVoltage = 0.1F;
	    if (outputVoltage > 10000)
	        outputVoltage = 10000;
	    
        if (outputResistance < 0)
            outputResistance = 0.001F;
        if (outputResistance > 100)
            outputResistance = 100;
        
        this.outputVoltage = outputVoltage;
        this.outputResistance = outputResistance;
		
        SEAPI.energyNetAgent.updateTileParameter(this);
        //No need to sync back, since the GUI is open and Container will do the job
	}
}
