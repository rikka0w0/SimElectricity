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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.*;
import simElectricity.API.Common.TileEntitySE;
import simElectricity.API.EnergyTile.IBaseComponent;
import simElectricity.API.EnergyTile.IConductor;
import simElectricity.API.EnergyTile.IConnectable;
import simElectricity.API.EnergyTile.IManualJunction;
import simElectricity.API.EnergyTile.ISEConductor;
import simElectricity.API.EnergyTile.ISEJunction;
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.EnergyTile.ISESubComponent;
import simElectricity.API.EnergyTile.ISETile;

import java.util.LinkedList;
import java.util.List;

public class TileSwitch extends TileEntitySE implements ISETile, IConnectable, ISidedFacing, IEnergyNetUpdateHandler, INetworkEventHandler {
    public class Switch implements ISEJunction{
    	private TileSwitch _te;
    	
    	public Switch(TileSwitch te){
    		_te = te;
    	}
    	
    	
		@Override
		public void getNeighbors(List<ISESimulatable> list) {
	        if (_te.isOn) {
	            TileEntity neighbor = Util.getTileEntityonDirection(_te, _te.inputSide);

	            if (neighbor instanceof ISEConductor)
	                list.add((ISEConductor) neighbor);

	            neighbor = Util.getTileEntityonDirection(_te, _te.outputSide);

	            if (neighbor instanceof ISEConductor)
	                list.add((ISEConductor) neighbor);
	        }
		}

		@Override
		public double getResistance(ISESimulatable neighbor) {
			return _te.resistance / 2;
		}
    	
	    public double getCurrent() {
	        if (!_te.isOn)
	            return 0;

	        TileEntity neighbor;
	        for (ForgeDirection dir : new ForgeDirection[] {_te.inputSide, _te.outputSide}) {
	            neighbor = Util.getTileEntityonDirection(_te, dir);
	            if (neighbor instanceof ISEConductor) {
	                return Math.abs((Energy.getVoltage(neighbor) - (Energy.getVoltage(this, _te.getWorldObj()))) /
	                        (((ISEConductor) neighbor).getResistance() + getResistance((ISEConductor)neighbor)));
	            }
	        }
	        return 0;
	    }
    }
	
	public double current=0F;
    
	public Switch sw = new Switch(this);
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
		            Energy.postTileRejoinEvent(this);
		            worldObj.notifyBlockChange(xCoord, yCoord, zCoord, 
		               		worldObj.getBlock(xCoord, yCoord, zCoord));
		        } else if (s.contains("resistance")) {
		            Energy.postTileChangeEvent(this);
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
    	current = sw.getCurrent();
        if (current > maxCurrent) {
            isOn = false;
            Energy.postTileRejoinEvent(this);
            Network.updateTileEntityFields(this, "isOn");
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
		return (side == inputSide || side == outputSide) ? sw : null;
	}
}
