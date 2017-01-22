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

package simElectricity.API.Common;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.ISEWrenchable;
import simElectricity.API.SEAPI;
import simElectricity.API.DataProvider.ISEVoltageSourceData;
import simElectricity.API.EnergyTile.ISESubComponent;
import simElectricity.API.Tile.ISETile;

/**
 * A standard SE machine can inherits this class, make things easier and less confusion
 */
public abstract class TileStandardSEMachine extends TileSidedFacingMachine implements ISEWrenchable, ISETile, ISEVoltageSourceData {
    public ForgeDirection functionalSide = ForgeDirection.NORTH;
    public ISESubComponent tile = (ISESubComponent) SEAPI.energyNetAgent.newComponent(this);
    
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
    public ForgeDirection getFunctionalSide() {
        return functionalSide;
    }

    @Override
    public void setFunctionalSide(ForgeDirection newFunctionalSide) {
        functionalSide = newFunctionalSide;
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
	public int getNumberOfComponents() {
		return 1;
	}

	@Override
	public ForgeDirection[] getValidDirections() {
		return new ForgeDirection[]{functionalSide};
	}

	@Override
	public ISESubComponent getComponent(ForgeDirection side) {
		return side == functionalSide ? tile : null;
	}
}
