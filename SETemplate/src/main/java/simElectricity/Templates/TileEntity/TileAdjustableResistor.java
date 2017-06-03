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

import net.minecraft.nbt.NBTTagCompound;
import simElectricity.API.SEAPI;

import simElectricity.Templates.Common.TileStandardSEMachine;
import simElectricity.Templates.Utils.IGuiSyncHandler;

public class TileAdjustableResistor extends TileStandardSEMachine implements IGuiSyncHandler {
    public double resistance = 1000;
    public double energyConsumed = 0;
    public double power = 0;

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote)
            return;

        double voltage = SEAPI.energyNetAgent.getVoltage(tile);
        power = voltage*voltage/resistance;
        energyConsumed += power / 20F;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        resistance = tagCompound.getDouble("resistance");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setDouble("resistance", resistance);
    }

    @Override
    public double getResistance() {
        return resistance;
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
		
		double resistance = this.resistance;
		
        switch (button) {
        case 0:
            if (isCtrlDown)
                resistance -= 100;
            else
                resistance -= 10;
            break;
        case 1:
            if (isCtrlDown)
                resistance -= 0.1;
            else
                resistance -= 1;
            break;
        case 2:
            if (isCtrlDown)
                resistance += 0.1;
            else
                resistance += 1;
            break;
        case 3:
            if (isCtrlDown)
                resistance += 100;
            else
                resistance += 10;
            break;
        default:
            energyConsumed = 0;
        }
        
        if (resistance < 0.1)
            resistance = 0.1F;
        if (resistance > 10000)
            resistance = 10000;
        
        this.resistance = resistance;
        
        SEAPI.energyNetAgent.updateTileParameter(this);
        //No need to sync back, since the GUI is open and Container will do the job
	}
}
