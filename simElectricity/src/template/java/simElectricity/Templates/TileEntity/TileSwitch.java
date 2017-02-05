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
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.*;
import simElectricity.API.DataProvider.ISEJunctionData;
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.EnergyTile.ISESubComponent;
import simElectricity.API.Tile.ISECableTile;
import simElectricity.API.Tile.ISETile;
import simElectricity.Templates.Common.TileEntitySE;

import java.util.LinkedList;
import java.util.List;

public class TileSwitch extends TileEntitySE implements ISETile, ISEJunctionData, ISEConnectable, ISidedFacing, IEnergyNetUpdateHandler, INetworkEventHandler {	
	ISESubComponent junction = (ISESubComponent) SEAPI.energyNetAgent.newComponent(this);
	public double current=0F;
    
    public ForgeDirection inputSide = ForgeDirection.NORTH, outputSide = ForgeDirection.SOUTH, facing = ForgeDirection.WEST;
    public float resistance = 0.005F;
    public float maxCurrent = 1F;
    public boolean isOn = false;

	@Override
	public boolean attachToEnergyNet() {
		return true;
	}

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        resistance = tagCompound.getFloat("resistance");
        maxCurrent = tagCompound.getFloat("maxCurrent");
        isOn = tagCompound.getBoolean("isOn");
        inputSide = ForgeDirection.getOrientation(tagCompound.getByte("inputSide"));
        outputSide = ForgeDirection.getOrientation(tagCompound.getByte("outputSide"));
        facing = ForgeDirection.getOrientation(tagCompound.getByte("facing"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setFloat("resistance", resistance);
        tagCompound.setFloat("maxCurrent", maxCurrent);
        tagCompound.setBoolean("isOn", isOn);
        tagCompound.setByte("inputSide", (byte) inputSide.ordinal());
        tagCompound.setByte("outputSide", (byte) outputSide.ordinal());
        tagCompound.setByte("facing", (byte) facing.ordinal());
    }

	@Override
	public void addNetworkFields(List fields) {

	}
    
	@Override
	public void onFieldUpdate(String[] fields, Object[] values) {
		if (worldObj.isRemote){
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}else{//Handling on server side
			for (String s:fields){
		        if (s.contains("inputSide") || s.contains("outputSide") || s.contains("isOn")) {
		        	SEAPI.energyNetAgent.reattachTile(this);
		            worldObj.notifyBlockChange(xCoord, yCoord, zCoord, 
		               		worldObj.getBlock(xCoord, yCoord, zCoord));
		        } else if (s.contains("resistance")) {
		        	SEAPI.energyNetAgent.markTileForUpdate(this);
		        } else if (s.contains("maxCurrent")) {
		            onEnergyNetUpdate();
		        }
			}
		}
	}

    @Override
    public boolean canConnectOnSide(ForgeDirection side) {
        return side == inputSide || side == outputSide;
    }

    @Override
    public ForgeDirection getFacing() {
        return facing;
    }

    @Override
    public void setFacing(ForgeDirection newFacing) {
        facing = newFacing;
    }

    @Override
    public boolean canSetFacing(ForgeDirection newFacing) {
        return newFacing != inputSide && newFacing != outputSide;
    }
	
    @Override
    public void onEnergyNetUpdate() {
    	current = SEAPI.energyNetAgent.getCurrentMagnitude(this.junction);
        if (current > maxCurrent) {
            isOn = false;
            SEAPI.energyNetAgent.reattachTile(this);
            SEAPI.networkManager.updateTileEntityFields(this, "isOn");
        }
    }



	@Override
	public int getNumberOfComponents() {
		return 1;
	}

	@Override
	public ForgeDirection[] getValidDirections() {
		return new ForgeDirection[]{inputSide, outputSide};
	}

	@Override
	public ISESubComponent getComponent(ForgeDirection side) {
		return (side == inputSide || side == outputSide) ? junction : null;
	}

	
	//Junction Data
	@Override
	public void getNeighbors(List<ISESimulatable> list) {
        if (isOn) {
            TileEntity neighbor = SEAPI.utils.getTileEntityonDirection(this, inputSide);

            if (neighbor instanceof ISECableTile)
                list.add(((ISECableTile) neighbor).getNode());

            neighbor = SEAPI.utils.getTileEntityonDirection(this, outputSide);

            if (neighbor instanceof ISECableTile)
            	list.add(((ISECableTile) neighbor).getNode());
        }
	}

	@Override
	public double getResistance(ISESimulatable neighbor) {
		return resistance / 2;
	}
	
	/*
    public double getCurrent() {
        if (!isOn)
            return 0;

        TileEntity neighbor;
        for (ForgeDirection dir : new ForgeDirection[] {inputSide, outputSide}) {
            neighbor = SEAPI.utils.getTileEntityonDirection(this, dir);
            if (neighbor instanceof ISECableTile) {
            	ISESimulatable node = ((ISECableTile)neighbor).getNode();
                // TODO QAQ!!!    
            	
                return Math.abs((SEAPI.energyNetAgent.getVoltage(node) - (SEAPI.energyNetAgent.getVoltage(junction))) /
                        (((ISECableTile) neighbor).getResistance() + getResistance(node)));
            	
            }
        }
        return 0;
    }
    */
}
