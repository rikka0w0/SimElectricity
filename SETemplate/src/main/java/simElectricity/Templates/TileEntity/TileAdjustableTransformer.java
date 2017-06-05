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
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISETransformerData;
import simelectricity.api.node.ISESubComponent;
import simelectricity.Templates.Common.TileEntityTwoPort;
import simelectricity.Templates.Utils.IGuiSyncHandler;

public class TileAdjustableTransformer extends TileEntityTwoPort implements ISETransformerData, IGuiSyncHandler {
    public ISESubComponent primary = SEAPI.energyNetAgent.newComponent(this, this);
    
    //Input - primary, output - secondary
    public double ratio = 10, outputResistance = 1;

	/////////////////////////////////////////////////////////
	///TileEntity
	/////////////////////////////////////////////////////////
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        ratio = tagCompound.getDouble("ratio");
        outputResistance = tagCompound.getDouble("outputResistance");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setDouble("ratio", ratio);
        tagCompound.setDouble("outputResistance", outputResistance);
    }

	/////////////////////////////////////////////////////////
	///ISETile
	/////////////////////////////////////////////////////////
	@Override
	public ISESubComponent getComponent(ForgeDirection side) {
		if (side == inputSide)
			return primary;
		if (side == outputSide)
			return primary.getComplement();
		return null;
	}
	
	/////////////////////////////////////////////////////////
	///ISETransformerData
	/////////////////////////////////////////////////////////
	@Override
	public double getRatio() {
		return ratio;
	}

	@Override
	public double getInternalResistance() {
		return outputResistance;
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
		
		double resistance = this.outputResistance;
		double ratio = this.ratio;
		
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
                ratio -= 100;
            else
                ratio -= 10;
            break;
        case 5:
            if (isCtrlDown)
                ratio -= 0.1;
            else
                ratio -= 1;
            break;
        case 6:
            if (isCtrlDown)
                ratio += 0.1;
            else
                ratio += 1;
            break;
        case 7:
            if (isCtrlDown)
                ratio += 100;
            else
                ratio += 10;
            break;

        default:
        }
        
        if (resistance < 0.001)
        	resistance = 0.001F;
        if (resistance > 100)
        	resistance = 100;


        if (ratio < 0.1)
            ratio = 0.1F;
        if (ratio > 1000)
            ratio = 1000;
        
        this.outputResistance = resistance;
        this.ratio = ratio;
        
        SEAPI.energyNetAgent.updateTileParameter(this);
	}
}
