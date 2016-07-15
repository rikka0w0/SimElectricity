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

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import simElectricity.API.Energy;
import simElectricity.API.IHVTower;
import simElectricity.API.INetworkEventHandler;
import simElectricity.API.Network;
import simElectricity.API.EnergyTile.ISEGridObject;
import simElectricity.API.EnergyTile.ISEGridTile;
import simElectricity.API.Events.*;

import java.util.LinkedList;
import java.util.List;

public class TileTower extends TileEntity implements ISEGridTile,INetworkEventHandler,IHVTower {
    public int facing;
    public int neighborsInfo[] = new int[] { 0, -1, 0, 0, -1, 0 };
	
    private boolean registered = false;
    private ISEGridObject gridObj = null;
    
    
    //ISEGridTile-----------------------------------------------------------------------------------------
    @Override
    public void setGridObject(ISEGridObject gridObj){
    	this.gridObj = gridObj;
    }
    
    @Override
    public void onGridNeighborUpdated(){
    	LinkedList<ISEGridObject> neighbors = gridObj.getNeighborList();
    	if (neighbors.size() == 0)
    		neighborsInfo = new int[] { 0, -1, 0, 0, -1, 0 };
    	else if (neighbors.size() == 1){
    		ISEGridObject neighbor1 = neighbors.get(0);
    		neighborsInfo = new int[] {neighbor1.getXCoord(),neighbor1.getYCoord(),neighbor1.getZCoord(), 
    								   0, -1, 0 };
    	}else if (neighbors.size() == 2){
    		ISEGridObject neighbor1 = neighbors.get(0);
    		ISEGridObject neighbor2 = neighbors.get(1);
    		neighborsInfo = new int[] {neighbor1.getXCoord(),neighbor1.getYCoord(),neighbor1.getZCoord(), 
    								   neighbor2.getXCoord(),neighbor2.getYCoord(),neighbor2.getZCoord() };
    	}
    	Network.updateNetworkFields(this);
    }
    
	@Override
	public boolean canConnect(){
		for (int i=0; i<neighborsInfo.length; i+=3){
			if (neighborsInfo[i+1] == -1)
				return true;
		}
		return false;
	}
	
	
    //INetworkEventHandler --------------------------------------------------------------------------------
	@Override
	public void onFieldUpdate(String[] fields, Object[] values) {
		//Handling on client side
		if (worldObj.isRemote){
			int i = 0;
			for (String s:fields){
		        if (s.contentEquals("neighborsInfo"))
		        	neighborsInfo = (int[]) values[i];
		        if (s.contentEquals("facing"))
		        	facing = (Integer) values[i];
		        i++;
			}
		}
	}

	@Override
	public void addNetworkFields(List fields) {
		fields.add("neighborsInfo");
		fields.add("facing");
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	
	//TileEntity -----------------------------------------------------------------------------------------
	@Override
    public void updateEntity() {
        super.updateEntity();
        
        //No client side operation
        if (worldObj.isRemote)
        	return;
        
        if (!registered){
        	Energy.postGridTilePresentEvent(this);
            registered = true;
        }
        	
	}
	
    @Override
    public void invalidate() {
    	super.invalidate();
    	
    	//No client side operation
        if (worldObj.isRemote)
        	return;
        
        if (registered){
        	Energy.postGridTileInvalidateEvent(this);
            registered = false;        	
        }
    }
	
    @Override
    public void onChunkUnload(){
    	invalidate();
    }   

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        facing = tagCompound.getInteger("facing");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setInteger("facing", facing);
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

    //IHVTower (rendering stuff) ----------------------------------------------------------------------
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
	
	@Override
	public float getWireTension() {
		switch (getBlockMetadata()){
		case 1:
			return 0.03f;
		case 2:
			return 0.03f;
		}
		return 0.06F;
	}


}
