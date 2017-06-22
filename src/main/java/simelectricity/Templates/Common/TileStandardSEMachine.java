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

package simelectricity.Templates.Common;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.api.ISEWrenchable;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.api.node.ISESubComponent;
import simelectricity.api.tile.ISETile;

/**
 * A standard SE machine can inherits this class, make things easier and less confusion
 */
public abstract class TileStandardSEMachine extends TileSidedFacingMachine implements ISEWrenchable, ISETile, ISEVoltageSource {
    public ForgeDirection functionalSide = ForgeDirection.NORTH;
    public ISESubComponent tile = SEAPI.energyNetAgent.newComponent(this, this);
    
    @Override
	public boolean attachToEnergyNet(){
    	return true;
    }
    
    // IEnergyTile
	@Override
	public double getOutputVoltage() {
		return 0;
	}
    
    @Override
    public void setFunctionalSide(ForgeDirection newFunctionalSide) {
    	boolean reJoinEnergyNet = (newFunctionalSide != functionalSide);
        functionalSide = newFunctionalSide;
        
        this.markTileEntityForS2CSync();
        this.worldObj.notifyBlockChange(xCoord, yCoord, zCoord, null);
        SEAPI.energyNetAgent.updateTileConnection(this);
    }

    @Override
    public boolean canSetFunctionalSide(ForgeDirection newFunctionalSide) {
        return newFunctionalSide != facing;
    }
    
    @Override
    public boolean canSetFacing(ForgeDirection newFacing) {
        return newFacing != functionalSide;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        functionalSide = ForgeDirection.getOrientation(tagCompound.getByte("functionalSide"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setByte("functionalSide", (byte) functionalSide.ordinal());
    }

    
    //ISETile
	@Override
	public ISESubComponent getComponent(ForgeDirection side) {
		return side == functionalSide ? tile : null;
	}
	
	
	/////////////////////////////////////////////////////////
	///Sync
	/////////////////////////////////////////////////////////
	@Override
	public void prepareS2CPacketData(NBTTagCompound nbt){	
		super.prepareS2CPacketData(nbt);
		
		nbt.setByte("functionalSide", (byte)functionalSide.ordinal());
	}
	
	@Override
	public void onSyncDataFromServerArrived(NBTTagCompound nbt){
		functionalSide = ForgeDirection.getOrientation(nbt.getByte("functionalSide"));
		
		super.onSyncDataFromServerArrived(nbt);
	}
}
