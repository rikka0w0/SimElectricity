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

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.Tile.ISECableTile;
import simElectricity.API.ITileRenderingInfoSyncHandler;
import simElectricity.API.SEAPI;
import simElectricity.Templates.Blocks.BlockWire;
import simElectricity.Templates.Common.TileEntitySE;
import simElectricity.Templates.Utils.Utils;

public class TileWire extends TileEntitySE implements ISECableTile, ITileRenderingInfoSyncHandler {
	public ISESimulatable node = SEAPI.energyNetAgent.newCable(this, false);
    protected boolean isAddedToEnergyNet = false;
    private boolean[] connections = new boolean[6];

    public int color = 0;
    public float resistance = 100;
    public float width = 0.1F;
    public String textureString;

    
    public boolean[] getConnections(){return connections;}
    
    public TileWire() {
    }

    public TileWire(int meta) {
        super();
        resistance = BlockWire.resistanceList[meta];
        width = BlockWire.renderingWidthList[meta];
        textureString = BlockWire.subNames[meta];
    }


    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        bb = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
        return bb;
    }


    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        resistance = tagCompound.getFloat("resistance");
        color = tagCompound.getInteger("color");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setFloat("resistance", resistance);
        tagCompound.setInteger("color", color);
    }

	@Override
	public boolean attachToEnergyNet() {
		return true;
	}
    
	
	///////////////////////////////////////
	///ISECableTile
	///////////////////////////////////////
    @Override
    public double getResistance() {
        return resistance;
    }

    @Override
    public int getColor() {
        return color;
    }
    
	@Override
	public ISESimulatable getNode() {
		return node;
	}
	
	@Override
	public boolean canConnectOnSide (ForgeDirection direction){
		return true;
	}
	
	@Override
	public boolean isGridLinkEnabled(){
		return false;
	}
	
	////////////////////////////////////////
	//Server->Client sync
	////////////////////////////////////////
	@Override
	public void sendRenderingInfoToClient() {
		//Update connection
        ForgeDirection[] dirs = ForgeDirection.values();
        for (int i = 0; i < 6; i++) {
        	connections[i] = Utils.canCableConnectTo(this, dirs[i]);
        }
		
		//Initiate Server->Client synchronization
		markTileEntityForS2CSync();
	}
	
	@Override
	public void prepareS2CPacketData(NBTTagCompound nbt){	
		super.prepareS2CPacketData(nbt);
		
		byte bc = 0x00;
		if (connections[0]) bc |= 1;
		if (connections[1]) bc |= 2;
		if (connections[2]) bc |= 4;
		if (connections[3]) bc |= 8;
		if (connections[4]) bc |= 16;
		if (connections[5]) bc |= 32;
		
		nbt.setByte("connections", bc);
	}
	
	@Override
	public void onSyncDataFromServerArrived(NBTTagCompound nbt){
		super.onSyncDataFromServerArrived(nbt);
		
		byte bc = nbt.getByte("connections");
		
		connections[0] = (bc & 1) > 0;
		connections[1] = (bc & 2) > 0;
		connections[2] = (bc & 4) > 0;
		connections[3] = (bc & 8) > 0;
		connections[4] = (bc & 16) > 0;
		connections[5] = (bc & 32) > 0;
		
		// Flag 1 - update Rendering Only!
		markForRenderUpdate();
	}
	

}
