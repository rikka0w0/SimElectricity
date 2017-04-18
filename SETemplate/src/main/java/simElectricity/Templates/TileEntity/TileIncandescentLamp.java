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
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.util.ForgeDirection;

import simElectricity.API.IEnergyNetUpdateHandler;
import simElectricity.API.ITileRenderingInfoSyncHandler;
import simElectricity.API.SEAPI;
import simElectricity.Templates.Common.TileStandardSEMachine;

import java.util.List;

public class TileIncandescentLamp extends TileStandardSEMachine implements IEnergyNetUpdateHandler, ITileRenderingInfoSyncHandler {
    public byte lightLevel = 0;
    
    @Override
    public boolean canSetFunctionalSide(ForgeDirection newFunctionalSide) {
        //FunctionalSide Facing
        return true;
    }

    @Override
    public double getOutputVoltage() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 9900; // 5 watt at 220V
    }
    
    @Override
    public void onEnergyNetUpdate() {
    	double voltage = SEAPI.energyNetAgent.getVoltage(tile);
        lightLevel = (byte) (voltage*voltage/getResistance() / 0.3F);
        if (lightLevel > 15)
            lightLevel = 15;
        
        //SEAPI.networkManager.updateNetworkFields(this);
        sendRenderingInfoToClient();
    }


	@Override
	public void sendRenderingInfoToClient() {
		this.markTileEntityForS2CSync();
	}
	
	@Override
	public void prepareS2CPacketData(NBTTagCompound nbt){	
		super.prepareS2CPacketData(nbt);
		
		nbt.setByte("lightLevel", lightLevel);
	}
	
	@Override
	public void onSyncDataFromServerArrived(NBTTagCompound nbt){
		super.onSyncDataFromServerArrived(nbt);
		lightLevel = nbt.getByte("lightLevel");
		this.markForRenderUpdate();
		worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);	//checkLightFor
	}
}
