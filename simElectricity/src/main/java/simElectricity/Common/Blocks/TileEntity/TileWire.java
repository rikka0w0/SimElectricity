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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.INetworkEventHandler;
import simElectricity.API.SEEnergy;
import simElectricity.API.Common.TileEntitySE;
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.Tile.ISECableTile;
import simElectricity.API.SEAPI;
import simElectricity.Common.Blocks.BlockWire;

public class TileWire extends TileEntitySE implements ISECableTile, INetworkEventHandler {
	public ISESimulatable node = SEAPI.energyNetAgent.newCable(this);
    protected boolean isAddedToEnergyNet = false;
    public boolean[] renderSides = new boolean[6];

    public int color = 0;
    public float resistance = 100;
    public float width = 0.1F;
    public String textureString;

    private int tick = 0;
    public boolean needsUpdate = false;
    
    public TileWire() {
    }

    public TileWire(int meta) {
        super();
        resistance = BlockWire.resistanceList[meta];
        width = BlockWire.renderingWidthList[meta];
        textureString = BlockWire.subNames[meta];
    }

    public void updateSides() {
        ForgeDirection[] dirs = ForgeDirection.values();
        for (int i = 0; i < 6; i++) {
            renderSides[i] = SEAPI.cableRenderHelper.canConnect(this, dirs[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        bb = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
        return bb;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        
        if (!worldObj.isRemote && needsUpdate){
        	tick++;
        	if(tick > 2){
        		needsUpdate = false;
        		tick = 0;
        		
        		SEAPI.networkManager.updateNetworkFields(this);
        	}
        }
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
    
    @Override
    public double getResistance() {
        return resistance;
    }

    @Override
    public int getColor() {
        return color;
    }

    public boolean isConnected(ForgeDirection direction) {
        return direction.ordinal() < 6 && direction.ordinal() >= 0 && renderSides[direction.ordinal()];
    }

	@Override
	public void addNetworkFields(List fields) {
		updateSides();
		fields.add("renderSides");
	}
	
	@Override
	public void onFieldUpdate(String[] fields, Object[] values) {

	}

	@Override
	public ISESimulatable getNode() {
		return node;
	}
}
