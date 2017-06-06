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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.api.IEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISESwitchData;
import simelectricity.api.node.ISESubComponent;
import simelectricity.Templates.Common.TileEntityTwoPort;
import simelectricity.Templates.Utils.IGuiSyncHandler;

public class TileSwitch extends TileEntityTwoPort implements ISESwitchData, IEnergyNetUpdateHandler, IGuiSyncHandler {	
	ISESubComponent switchComponent = SEAPI.energyNetAgent.newComponent(this, this);
	public double current=0F;
    
    public double resistance = 0.005F;
    public double maxCurrent = 1F;
    public boolean isOn = false;

	/////////////////////////////////////////////////////////
	///TileEntity
	/////////////////////////////////////////////////////////
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        resistance = tagCompound.getDouble("resistance");
        maxCurrent = tagCompound.getDouble("maxCurrent");
        isOn = tagCompound.getBoolean("isOn");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setDouble("resistance", resistance);
        tagCompound.setDouble("maxCurrent", maxCurrent);
        tagCompound.setBoolean("isOn", isOn);
    }

	/////////////////////////////////////////////////////////
	///IEnergyNetUpdateHandler
	/////////////////////////////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
    	if (isOn){
        	double vA = SEAPI.energyNetAgent.getVoltage(this.switchComponent);
        	double vB = SEAPI.energyNetAgent.getVoltage(this.switchComponent.getComplement());
        	current = Math.abs(vA-vB)/resistance;
    	}else{
    		current = 0;
    	}

        if (current > maxCurrent) {
            isOn = false;
            SEAPI.energyNetAgent.updateTileConnection(this);
            
            this.markTileEntityForS2CSync();
        }
    }

	/////////////////////////////////////////////////////////
	///ISETile
	/////////////////////////////////////////////////////////
	@Override
	public ISESubComponent getComponent(ForgeDirection side) {
		if (side == inputSide)
			return switchComponent;
		else if (side == outputSide)
			return switchComponent.getComplement();
		else
			return null;
	}

	/////////////////////////////////////////////////////////
	///ISESwitchData
	/////////////////////////////////////////////////////////
	@Override
	public boolean isOn(){
		return isOn;
	}

	@Override
	public double getResistance() {
		return resistance;
	}
	
	/////////////////////////////////////////////////////////
	///Sync
	/////////////////////////////////////////////////////////
	@Override
	public void prepareS2CPacketData(NBTTagCompound nbt){	
		super.prepareS2CPacketData(nbt);
		
		nbt.setBoolean("isOn", isOn);
	}
	
	@Override
	public void onSyncDataFromServerArrived(NBTTagCompound nbt){	
		isOn = nbt.getBoolean("isOn");

		super.onSyncDataFromServerArrived(nbt);
	}

	/////////////////////////////////////////////////////////
	///IGuiSyncHandler
	/////////////////////////////////////////////////////////
	@Override
	public void onGuiEvent(byte eventID, Object[] data) {
		if (eventID == IGuiSyncHandler.EVENT_FACING_CHANGE){
			byte button = (Byte) data[0];
			ForgeDirection selectedDirection = (ForgeDirection) data[1];
			
		    if (button == 0) {        //Left key
		        if (outputSide == selectedDirection)
		            outputSide = inputSide;
		        inputSide = selectedDirection;
		    } else if (button == 1) { //Right key
		        if (inputSide == selectedDirection)
		            inputSide = outputSide;
		        outputSide = selectedDirection;
	        }

            SEAPI.energyNetAgent.updateTileConnection(this);
			this.markTileEntityForS2CSync();
			this.worldObj.notifyBlockChange(xCoord, yCoord, zCoord, null);
			return;
		}
		
		//EVENT_BUTTON_CLICK
		boolean isCtrlDown = (Boolean) data[0];
		byte button = (Byte) data[1];
		
		double resistance = this.resistance;
		double maxCurrent = this.maxCurrent;
		boolean isOn = this.isOn;
		boolean reattch = false;
		
        switch (button) {
        case 0:
            if (isCtrlDown)
                resistance -= 1;
            else
                resistance -= 0.1;
            break;
        case 1:
            if (isCtrlDown)
                resistance -= 0.001;
            else
                resistance -= 0.01;
            break;
        case 2:
            if (isCtrlDown)
                resistance += 0.001;
            else
                resistance += 0.01;
            break;
        case 3:
            if (isCtrlDown)
                resistance += 1;
            else
                resistance += 0.1;
            break;

        case 4:
            if (isCtrlDown)
                maxCurrent -= 100;
            else
                maxCurrent -= 10;
            break;
        case 5:
            if (isCtrlDown)
                maxCurrent -= 0.1;
            else
                maxCurrent -= 1;
            break;
        case 6:
            if (isCtrlDown)
                maxCurrent += 0.1;
            else
                maxCurrent += 1;
            break;
        case 7:
            if (isCtrlDown)
                maxCurrent += 100;
            else
                maxCurrent += 10;
            break;
        case 8:
        	reattch = true;
        	isOn = !isOn;
        	break;
        default:
        }

	    if (resistance < 0.001)
	        resistance = 0.001F;
	    if (resistance > 100)
	        resistance = 100;
	    
	    if (maxCurrent < 0.1)
	        maxCurrent = 0.1F;
	    if (maxCurrent > 1000)
	        maxCurrent = 1000;
			
	    this.resistance = resistance;
	    this.maxCurrent = maxCurrent;
	    this.isOn = isOn;
	    
	    
	    SEAPI.energyNetAgent.updateTileConnection(this);

	    //onEnergyNetUpdate();		//Check trip-off
	    this.markTileEntityForS2CSync();
	}
}
