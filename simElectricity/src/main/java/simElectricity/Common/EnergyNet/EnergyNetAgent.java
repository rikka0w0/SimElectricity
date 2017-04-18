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

package simElectricity.Common.EnergyNet;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.Map;
import java.util.WeakHashMap;

import simElectricity.API.ISEPlaceable;
import simElectricity.API.DataProvider.ISEComponentDataProvider;
import simElectricity.API.DataProvider.ISEConstantPowerLoadData;
import simElectricity.API.DataProvider.ISEDiodeData;
import simElectricity.API.DataProvider.ISEJunctionData;
import simElectricity.API.DataProvider.ISERegulatorData;
import simElectricity.API.DataProvider.ISETransformerData;
import simElectricity.API.DataProvider.ISEVoltageSourceData;
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.EnergyTile.ISESubComponent;
import simElectricity.Common.ConfigManager;
import simElectricity.Common.SEUtils;
import simElectricity.API.Internal.IEnergyNetAgent;
import simElectricity.API.Tile.ISECableTile;
import simElectricity.API.Tile.ISEGridTile;
import simElectricity.Common.EnergyNet.Components.Cable;
import simElectricity.Common.EnergyNet.Components.ConstantPowerLoad;
import simElectricity.Common.EnergyNet.Components.DiodeInput;
import simElectricity.Common.EnergyNet.Components.Junction;
import simElectricity.Common.EnergyNet.Components.RegulatorInput;
import simElectricity.Common.EnergyNet.Components.SEComponent;
import simElectricity.Common.EnergyNet.Components.TransformerPrimary;
import simElectricity.Common.EnergyNet.Components.VoltageSource;

public class EnergyNetAgent implements IEnergyNetAgent{
    @SuppressWarnings("unchecked")
    public static Map<World, EnergyNet> mapping = new WeakHashMap();

    /**
     * Return the instance of energyNet for a specific world
     * <p/>
     * If target not exist, it will automatically be created
     */
    public static EnergyNet getEnergyNetForWorld(World world) {
        if (world == null)
            throw new IllegalArgumentException("world is null");

        EnergyNet ret = mapping.get(world);

        if (ret == null) {
        	ret = new EnergyNet(world);
            mapping.put(world, ret);
        }

        return ret;
    }

    public static void onWorldUnload(World world) {
        mapping.remove(world);
    }
    
    @Override
    public double getVoltage(ISESimulatable Tile) {
    	SEComponent obj = (SEComponent) Tile;
        return EnergyNetAgent.getEnergyNetForWorld(obj.te.getWorldObj()).getVoltage(Tile);
    }

    @Override
    public double getCurrentMagnitude(ISESimulatable Tile){
    	SEComponent obj = (SEComponent) Tile;
        return EnergyNetAgent.getEnergyNetForWorld(obj.te.getWorldObj()).getCurrentMagnitude(Tile);  	
    }
    
	@Override
	public ISESubComponent newComponent(TileEntity dataProviderTileEntity) {
		return newComponent((ISEComponentDataProvider)dataProviderTileEntity, dataProviderTileEntity);
	}
	
    
	@Override
	public ISESubComponent newComponent(ISEComponentDataProvider dataProvider, TileEntity parent) {
		if (dataProvider instanceof ISEDiodeData)
			//Create a DiodeInput and DiodeOutput at the same time
			return new DiodeInput((ISEDiodeData) dataProvider, parent);
		else if (dataProvider instanceof ISETransformerData)
			return new TransformerPrimary((ISETransformerData) dataProvider, parent);
		else if (dataProvider instanceof ISERegulatorData)
			return new RegulatorInput((ISERegulatorData) dataProvider, parent);
		else if (dataProvider instanceof ISEConstantPowerLoadData)
			return new ConstantPowerLoad((ISEConstantPowerLoadData) dataProvider, parent);
		else if (dataProvider instanceof ISEJunctionData)
			return new Junction((ISEJunctionData) dataProvider, parent);
		else if (dataProvider instanceof ISEVoltageSourceData)
			return new VoltageSource((ISEVoltageSourceData) dataProvider, parent);
		
		return null;
	}
	
	@Override
	public ISESimulatable newCable(TileEntity dataProviderTileEntity){
		if (dataProviderTileEntity instanceof ISECableTile)
			return new Cable((ISECableTile) dataProviderTileEntity, dataProviderTileEntity);
		return null;
	}

	@Override
    public void attachTile(TileEntity te) {
        if (!te.getWorldObj().blockExists(te.xCoord, te.yCoord, te.zCoord)) {
            SEUtils.logInfo(te + " is added to the energy net too early!, abort!");
            return;
        }

        if (te.isInvalid()) {
            SEUtils.logInfo("Invalid tileentity " + te + " is trying to attach to the energy network, aborting");
            return;
        }
        
        if (te.getWorldObj().isRemote) {
            SEUtils.logInfo("Client tileentity " + te + " is requesting, abort!");
            return;
        }
        
        World world = te.getWorldObj();
        if (te instanceof ISEGridTile) {
        	EnergyNetDataProvider grid = EnergyNetDataProvider.get(world);
        	grid.onGridTilePresent(te);
        	
        	if (ConfigManager.showEnergyNetInfo)
        		SEUtils.logInfo("GridTile assosiated with GridObject at "+String.valueOf(te.xCoord)+","+String.valueOf(te.yCoord)+","+String.valueOf(te.zCoord));
        }
        
        if (te instanceof ISEPlaceable) {
	        EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(world);
	        energyNet.addTileEntity(te);
	
	        if (ConfigManager.showEnergyNetInfo)
	            SEUtils.logInfo("Tileentity " + te + " has attached to the energy network!");        
        }
    }

	@Override
    public void markTileForUpdate(TileEntity te) {
        if (te.getWorldObj().isRemote) {
            SEUtils.logInfo("Client tileentity " + te + " is requesting, aborting");
            return;
        }

        EnergyNetAgent.getEnergyNetForWorld(te.getWorldObj()).markForUpdate(te);

        if (ConfigManager.showEnergyNetInfo)
            SEUtils.logInfo("Tileentity " + te + " causes the energy network to update!");
    }
	
	@Override
    public void detachTile(TileEntity te) {
        if (te.getWorldObj().isRemote) {
            SEUtils.logInfo("Client tileentity " + te + " is requesting, abort!");
            return;
        }

        World world = te.getWorldObj();
        if (te instanceof ISEGridTile) {
        	EnergyNetDataProvider grid = EnergyNetDataProvider.get(world);
        	grid.onGridTileInvalidate(te);
        	
        	if (ConfigManager.showEnergyNetInfo)
        		SEUtils.logInfo("GridTile destroyed at"+String.valueOf(te.xCoord)+","+String.valueOf(te.yCoord)+","+String.valueOf(te.zCoord));
        }
        
        if (te instanceof ISEPlaceable) {
	        EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(world);
	        energyNet.removeTileEntity(te);        	
	        if (ConfigManager.showEnergyNetInfo)
	            SEUtils.logInfo("Tileentity " + te + " has detached from the energy network!");
        }
    }

	@Override
    public void reattachTile(TileEntity te) {
        if (te.getWorldObj().isRemote) {
            SEUtils.logInfo("Client tileentity " + te + " is requesting, abort!");
            return;
        }

        EnergyNetAgent.getEnergyNetForWorld(te.getWorldObj()).rejoinTileEntity(te);

        if (ConfigManager.showEnergyNetInfo)
            SEUtils.logInfo("Tileentity " + te + " has rejoined the energy network!");
    }
    
	@Override
    public void attachGridObject(World world, int x, int y, int z, byte type) {
    	EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(world);
    	
    	if (energyNet.addGridNode(x, y, z, type)){
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("GridObject attached at " +String.valueOf(x)+":"+String.valueOf(y)+":"+String.valueOf(z));
    	}else{
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("Fail to attach gridObject at " +String.valueOf(x)+":"+String.valueOf(y)+":"+String.valueOf(z));
    	}
    }
    
	@Override
    public void detachGridObject(World world, int x, int y, int z) {
    	EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(world);

    	if (energyNet.removeGridNode(x, y, z)){
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("GridObject detached at "+String.valueOf(x)+":"+String.valueOf(y)+":"+String.valueOf(z));
    	}else{
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("Fail to detach gridObject at " +String.valueOf(x)+":"+String.valueOf(y)+":"+String.valueOf(z));
    	}
    }
    
	@Override
    public void connectGridNode(World world, int x1, int y1, int z1, int x2, int y2, int z2, double resistance) {
    	EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(world);
       	
    	if (energyNet.addGridConnection(x1, y1, z1, x2, y2, z2, resistance)){
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("Grid connection built between " +String.valueOf(x1)+":"+String.valueOf(y1)+":"+String.valueOf(z1)+" and "
					+String.valueOf(x2)+":"+String.valueOf(y2)+":"+String.valueOf(z2));
    	}else{
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("Fail to build grid connection between " +String.valueOf(x1)+":"+String.valueOf(y1)+":"+String.valueOf(z1)+" and "
					+String.valueOf(x2)+":"+String.valueOf(y2)+":"+String.valueOf(z2));
    	}
    }   
    
    public void breakGridConnection(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
    	EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(world);
    	
    	if (energyNet.removeGridConnection(x1, y1, z1, x2, y2, z2)){
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("Grid connection removed between " +String.valueOf(x1)+","+String.valueOf(y1)+","+String.valueOf(z1)+" and "
				+String.valueOf(x2)+","+String.valueOf(y2)+","+String.valueOf(z2));
    	}else{
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("Fail to remove grid connection between " +String.valueOf(x1)+","+String.valueOf(y1)+","+String.valueOf(z1)+" and "
				+String.valueOf(x2)+","+String.valueOf(y2)+","+String.valueOf(z2));
    	}
    }
}