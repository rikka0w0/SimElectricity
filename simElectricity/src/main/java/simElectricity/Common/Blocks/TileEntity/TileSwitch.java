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

import java.util.List;

public class TileSwitch extends TileEntitySE implements IManualJunction, IConnectable, ISidedFacing, IEnergyNetUpdateHandler, INetworkEventHandler {
    protected boolean isAddedToEnergyNet = false;
    public float current=0F;
    
    public ForgeDirection inputSide = ForgeDirection.NORTH, outputSide = ForgeDirection.SOUTH, facing = ForgeDirection.WEST;
    public float resistance = 0.1F;
    public float maxCurrent = 1F;
    public boolean isOn = false;

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!worldObj.isRemote && !isAddedToEnergyNet) {
            Energy.postTileAttachEvent(this);
            this.isAddedToEnergyNet = true;
        }
    }

    @Override
    public void invalidate() {
        if (!worldObj.isRemote & isAddedToEnergyNet) {
            Energy.postTileDetachEvent(this);
            this.isAddedToEnergyNet = false;
        }

        super.invalidate();
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
		//Handling on server side
		if (!worldObj.isRemote){
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
    public float getResistance() {
        return resistance;
    }

    @Override
    public boolean canConnectOnSide(ForgeDirection side) {
        return side == inputSide || side == outputSide;
    }

    @Override
    public void addNeighbors(List<IBaseComponent> list) {
        if (isOn) {
            TileEntity neighbor = Util.getTileEntityonDirection(this, inputSide);

            if (neighbor instanceof IConductor)
                list.add((IConductor) neighbor);

            neighbor = Util.getTileEntityonDirection(this, outputSide);

            if (neighbor instanceof IConductor)
                list.add((IConductor) neighbor);
        }
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
    	current = getCurrent();
        if (current > maxCurrent) {
            isOn = false;
            Energy.postTileRejoinEvent(this);
            Network.updateTileEntityField(this, "isOn");
        }
    }

    private float getCurrent() {
        if (!isOn)
            return 0;

        TileEntity neighbor;
        for (ForgeDirection dir : new ForgeDirection[] { inputSide, outputSide }) {
            neighbor = Util.getTileEntityonDirection(this, dir);
            if (neighbor instanceof IConductor) {
                return 2F * Math.abs((Energy.getVoltage((IConductor) neighbor) - (Energy.getVoltage(this))) /
                        (((IConductor) neighbor).getResistance() + this.getResistance()));
            }
        }
        return 0;
    }

	@Override
	public float getResistance(IBaseComponent neighbor) {
		return 0;
	}
}
