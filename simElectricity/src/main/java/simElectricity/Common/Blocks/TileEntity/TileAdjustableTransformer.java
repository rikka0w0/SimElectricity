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

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.SEAPI;
import simElectricity.API.Common.TileEntitySE;
import simElectricity.API.DataProvider.ISETransformerData;
import simElectricity.API.EnergyTile.ISESubComponent;
import simElectricity.API.Tile.ISETile;
import simElectricity.API.INetworkEventHandler;

public class TileAdjustableTransformer extends TileEntitySE implements ISETile, ISETransformerData, INetworkEventHandler {
    public ISESubComponent primary = (ISESubComponent) SEAPI.energyNetAgent.newComponent(this);
    
    public ForgeDirection primarySide = ForgeDirection.NORTH, secondarySide = ForgeDirection.SOUTH;
    public float ratio = 10, outputResistance = 1;

    @Override
	public boolean attachToEnergyNet(){
    	return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        ratio = tagCompound.getFloat("ratio");
        outputResistance = tagCompound.getFloat("outputResistance");
        primarySide = ForgeDirection.getOrientation(tagCompound.getByte("primarySide"));
        secondarySide = ForgeDirection.getOrientation(tagCompound.getByte("secondarySide"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setFloat("ratio", ratio);
        tagCompound.setFloat("outputResistance", outputResistance);
        tagCompound.setByte("primarySide", (byte) primarySide.ordinal());
        tagCompound.setByte("secondarySide", (byte) secondarySide.ordinal());
    }

	@Override
	public void onFieldUpdate(String[] fields, Object[] values) {
		//Handling on server side
		if (!worldObj.isRemote){
			for (String s:fields){
		        if (s.contains("primarySide") || s.contains("secondarySide")) {
		            SEAPI.energyNetAgent.reattachTile(this);
		            worldObj.notifyBlockChange(xCoord, yCoord, zCoord, 
		            		worldObj.getBlock(xCoord, yCoord, zCoord));
		        } else if (s.contains("outputResistance") || s.contains("ratio")) {
		        	SEAPI.energyNetAgent.markTileForUpdate(this);
		        }				
			}

		}
	}

	@Override
	public void addNetworkFields(List fields) {

	}

	@Override
	public int getNumberOfComponents() {
		return 2;
	}

	@Override
	public ForgeDirection[] getValidDirections() {
		return new ForgeDirection[]{primarySide, secondarySide};
	}

	@Override
	public ISESubComponent getComponent(ForgeDirection side) {
		if (side == primarySide)
			return primary;
		if (side == secondarySide)
			return primary.getComplement();
		return null;
	}
	
	public ForgeDirection getPrimarySide(){
		return primarySide;
	}
	
	public ForgeDirection getSecondarySide(){
		return secondarySide;
	}

	//Transformer data
	@Override
	public double getRatio() {
		return ratio;
	}

	@Override
	public double getInternalResistance() {
		return outputResistance;
	}
}
