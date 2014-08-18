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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import simElectricity.API.Energy;
import simElectricity.API.IHVTower;
import simElectricity.API.Network;
import simElectricity.API.Common.TileEntitySE;
import simElectricity.API.EnergyTile.IBaseComponent;
import simElectricity.API.EnergyTile.IManualJunction;

import java.util.List;

public class TileTower extends TileEntitySE implements IManualJunction, IHVTower {
    public int facing;
    public int neighborsInfo[] = new int[] { 0, -1, 0, 0, -1, 0 };
	
	@Override
	public void addNeighbor(TileEntity te){
		for (int i=0; i<neighborsInfo.length; i+=3){
			if (neighborsInfo[i+1] == -1){
				neighborsInfo[i] = te.xCoord;
				neighborsInfo[i+1] = te.yCoord;
				neighborsInfo[i+2] = te.zCoord;
				Energy.postTileRejoinEvent(this);
				return;
			}
		}
	}
	
	public void delNeighbor(TileEntity te){
		for (int i=0;i<neighborsInfo.length;i+=3){
			if (neighborsInfo[i] == te.xCoord &&
				neighborsInfo[i+1] == te.yCoord &&
				neighborsInfo[i+2] == te.zCoord){
				neighborsInfo[i] = 0;
				neighborsInfo[i+1] = -1;
				neighborsInfo[i+2] = 0;				
			}	
		}
		Energy.postTileRejoinEvent(this);
	}
	
	@Override
	public boolean hasVacant(){
		for (int i=0; i<neighborsInfo.length; i+=3){
			if (neighborsInfo[i+1] == -1)
				return true;
		}
		return false;
	}
	
	@SideOnly(Side.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared()
    {
        return 100000;
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public AxisAlignedBB getRenderBoundingBox(){
    	return INFINITE_EXTENT_AABB;
    }

	@Override
	public boolean attachToEnergyNet() {
		return true;
	}
    
	@Override
	public void onUnload(){
        for (int i = 0; i < neighborsInfo.length; i += 3) {
            TileTower neighbor = (TileTower) getWorldObj().getTileEntity(neighborsInfo[i], neighborsInfo[i + 1], neighborsInfo[i + 2]);
            if (neighbor != null) {
                neighbor.delNeighbor(this);
                Network.updateTileEntityNBT(this);
            }
        }		
	}

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        facing = tagCompound.getInteger("facing");
        neighborsInfo = tagCompound.getIntArray("neighborsInfo");
        if (neighborsInfo == null)
            neighborsInfo = new int[] { 0, -1, 0, 0, -1, 0 };
        if (neighborsInfo.length != 6)
            neighborsInfo = new int[] { 0, -1, 0, 0, -1, 0 };
        
        if (xCoord==127)
        	return;
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setInteger("facing", facing);
        tagCompound.setIntArray("neighborsInfo", this.neighborsInfo);
    }

    @Override
    public double getResistance() {
        return 0;
    }


    @Override
    public void addNeighbors(List<IBaseComponent> list) {
        TileEntity te;
        if (neighborsInfo[1] != -1) {
            te = worldObj.getTileEntity(neighborsInfo[0], neighborsInfo[1], neighborsInfo[2]);
            if (te instanceof TileTower)
                list.add((IManualJunction) te);
        }

        if (neighborsInfo[4] != -1) {
            te = worldObj.getTileEntity(neighborsInfo[3], neighborsInfo[4], neighborsInfo[5]);
            if (te instanceof TileTower)
                list.add((IManualJunction) te);
        }
        
        if (this.getBlockMetadata() == 1){
        	te = worldObj.getTileEntity(xCoord,yCoord - 2,zCoord);
        	if (te instanceof TileTower && te.getBlockMetadata() == 2)
        		list.add((IManualJunction) te);
        	
        	if (te instanceof TileCableClamp)
        		list.add((IManualJunction) te);
        }
        
        if (this.getBlockMetadata() == 2){
        	te = worldObj.getTileEntity(xCoord,yCoord + 2,zCoord);
        	if (te instanceof TileTower && te.getBlockMetadata() == 1)
        		list.add((IManualJunction) te);
        }
    }

    @Override
    public double getResistance(IBaseComponent neighbor) {
        if (neighbor == worldObj.getTileEntity(xCoord,yCoord - 2,zCoord))
            return 0.1F;

        if (neighbor instanceof TileTower) {
            return (float) (0.2 * Math.sqrt(getDistanceFrom(((TileEntity) neighbor).xCoord, ((TileEntity) neighbor).yCoord, ((TileTower) neighbor).zCoord)));
        }

        return Float.MAX_VALUE;
    }

	@Override
	public float[] offsetArray() {
		switch (getBlockMetadata()){
		case 0:return new float[]{-3, 3, 0, 0, 3, 0, 3, 3, 0};
		case 1:return new float[]{-1, -0.55F, 0, 1, 0.95F, 0, 1.5F, -0.55F, 0};
		case 2:return new float[]{-1.5F, 0, 0.2F, 0.5F, 0, 0.2F, 1.5F, 0, 0.2F};
		default: return null;
		}
	}

	@Override
	public int[] getNeighborInfo() {
		return neighborsInfo;
	}
	
	@Override
	public int getFacing(){
		return facing;
	}
}
