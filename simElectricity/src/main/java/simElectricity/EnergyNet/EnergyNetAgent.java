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

package simElectricity.EnergyNet;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Map;
import java.util.WeakHashMap;

import simElectricity.API.ISEPlaceable;
import simElectricity.API.DataProvider.ISEComponentDataProvider;
import simElectricity.API.DataProvider.ISEConstantPowerLoadData;
import simElectricity.API.DataProvider.ISEDiodeData;
import simElectricity.API.DataProvider.ISERegulatorData;
import simElectricity.API.DataProvider.ISESwitchData;
import simElectricity.API.DataProvider.ISETransformerData;
import simElectricity.API.DataProvider.ISEVoltageSourceData;
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.EnergyTile.ISESubComponent;
import simElectricity.Common.ConfigManager;
import simElectricity.Common.SEUtils;
import simElectricity.API.Internal.IEnergyNetAgent;
import simElectricity.API.Tile.ISECableTile;
import simElectricity.API.Tile.ISEGridTile;
import simElectricity.API.Tile.ISETile;
import simElectricity.EnergyNet.Components.Cable;
import simElectricity.EnergyNet.Components.ConstantPowerLoad;
import simElectricity.EnergyNet.Components.DiodeInput;
import simElectricity.EnergyNet.Components.RegulatorInput;
import simElectricity.EnergyNet.Components.SEComponent;
import simElectricity.EnergyNet.Components.SwitchA;
import simElectricity.EnergyNet.Components.TransformerPrimary;
import simElectricity.EnergyNet.Components.VoltageSource;

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
    	//mapping.get(world).shutdown();
        mapping.remove(world);
    }
    
    @Override
    public double getVoltage(ISESimulatable Tile) {
    	SEComponent obj = (SEComponent) Tile;
        return EnergyNetAgent.getEnergyNetForWorld(obj.te.getWorldObj()).getVoltage(Tile);
    }
    
    @Override
	public boolean canConnectTo(TileEntity tileEntity, ForgeDirection direction) {
		if (tileEntity instanceof ISECableTile){
			ISECableTile cableTile = (ISECableTile) tileEntity;
			TileEntity neighborTileEntity = SEUtils.getTileEntityOnDirection(tileEntity, direction);
			
            if (neighborTileEntity instanceof ISECableTile) {
            	ISECableTile neighborCableTile = (ISECableTile) neighborTileEntity;
				return 	(
						cableTile.getColor() == 0 ||
								neighborCableTile.getColor() == 0 ||
	                 	cableTile.getColor() == neighborCableTile.getColor()
						)&&(
	            		cableTile.canConnectOnSide(direction) &&
	            		neighborCableTile.canConnectOnSide(direction.getOpposite())
						);
            }else if (neighborTileEntity instanceof ISETile){
            	return ((ISETile)neighborTileEntity).getComponent(direction.getOpposite()) != null;
            }
		}else if (tileEntity instanceof ISETile){
			ISETile tile = (ISETile)tileEntity;
			TileEntity neighborTileEntity = SEUtils.getTileEntityOnDirection(tileEntity, direction);
			
            if (neighborTileEntity instanceof ISECableTile)
            	return ((Cable)((ISECableTile)neighborTileEntity).getNode()).canConnectOnSide(direction.getOpposite());
		}else{
			throw new RuntimeException("canConnectTo: input parameter \"tileEntity\" must implement either ISECableTile or ISETile");
		}
		
        return false;
    }
    
	@Override
	public ISESubComponent newComponent(ISEComponentDataProvider dataProvider, TileEntity parent) {
		if (dataProvider instanceof ISEDiodeData)
			//Create a pair of DiodeInput and DiodeOutput at the same time
			return new DiodeInput((ISEDiodeData) dataProvider, parent);
		else if (dataProvider instanceof ISETransformerData)
			return new TransformerPrimary((ISETransformerData) dataProvider, parent);
		else if (dataProvider instanceof ISERegulatorData)
			return new RegulatorInput((ISERegulatorData) dataProvider, parent);
		else if (dataProvider instanceof ISEConstantPowerLoadData)
			return new ConstantPowerLoad((ISEConstantPowerLoadData) dataProvider, parent);
		else if (dataProvider instanceof ISEVoltageSourceData)
			return new VoltageSource((ISEVoltageSourceData) dataProvider, parent);
		else if (dataProvider instanceof ISESwitchData)
			return new SwitchA(((ISESwitchData)dataProvider), parent);
		return null;
	}
	
	@Override
	public ISESimulatable newCable(TileEntity dataProviderTileEntity, boolean isGridInterConnectionPoint){
		if (dataProviderTileEntity instanceof ISECableTile)
			return new Cable((ISECableTile) dataProviderTileEntity, dataProviderTileEntity, isGridInterConnectionPoint);
		return null;
	}

	@Override
    public void attachTile(TileEntity te) {
        if (!te.getWorldObj().blockExists(te.xCoord, te.yCoord, te.zCoord)) {
            SEUtils.logInfo(te + " is added to the energy net too early!, abort!", SEUtils.energyTile);
            return;
        }

        if (te.isInvalid()) {
            SEUtils.logInfo("Invalid tileentity " + te + " is trying to attach to the energy network, aborting", SEUtils.energyTile);
            return;
        }
        
        if (te.getWorldObj().isRemote) {
            SEUtils.logInfo("Client tileentity " + te + " is requesting, abort!", SEUtils.energyTile);
            return;
        }
        
        World world = te.getWorldObj();
        EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(world);
        if (te instanceof ISEGridTile) {
        	energyNet.addEvent(new TileEvent.GridTilePresent(te));
        	
        	if (ConfigManager.showEnergyNetInfo)
        		SEUtils.logInfo("GridTile linked with GridObject at "+String.valueOf(te.xCoord)+","+String.valueOf(te.yCoord)+","+String.valueOf(te.zCoord), SEUtils.grid);
        }
        
        if (te instanceof ISEPlaceable) {
	        energyNet.addEvent(new TileEvent.Attach(te));
	
	        if (ConfigManager.showEnergyNetInfo)
	            SEUtils.logInfo("Tileentity " + te + " has attached to the energy network!", SEUtils.energyTile);        
        }
    }

	@Override
    public void updateTileParameter(TileEntity te) {
        if (te.getWorldObj().isRemote) {
            SEUtils.logInfo("Client tileentity " + te + " is requesting, aborting", SEUtils.energyTile);
            return;
        }

        EnergyNetAgent.getEnergyNetForWorld(te.getWorldObj()).addEvent(new TileEvent.ParamChanged(te));

        if (ConfigManager.showEnergyNetInfo)
            SEUtils.logInfo("Tileentity " + te + " causes the energy network to update!", SEUtils.energyTile);
    }
	
	@Override
    public void detachTile(TileEntity te) {
        if (te.getWorldObj().isRemote) {
            SEUtils.logInfo("Client tileentity " + te + " is requesting, abort!", SEUtils.energyTile);
            return;
        }

        World world = te.getWorldObj();
        EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(world);
        if (te instanceof ISEGridTile) {
        	energyNet.addEvent(new TileEvent.GridTileInvalidate(te)); 
        	
        	if (ConfigManager.showEnergyNetInfo)
        		SEUtils.logInfo("GridTile destroyed at"+String.valueOf(te.xCoord)+","+String.valueOf(te.yCoord)+","+String.valueOf(te.zCoord), SEUtils.grid);
        }
        
        if (te instanceof ISEPlaceable) {
	        energyNet.addEvent(new TileEvent.Detach(te));     	
	        if (ConfigManager.showEnergyNetInfo)
	            SEUtils.logInfo("Tileentity " + te + " has detached from the energy network!", SEUtils.energyTile);
        }
    }

	@Override
    public void updateTileConnection(TileEntity te) {
        if (te.getWorldObj().isRemote) {
        	throw new RuntimeException("Server-only API is called from client side!");
        }

        EnergyNetAgent.getEnergyNetForWorld(te.getWorldObj()).addEvent(new TileEvent.ConnectionChanged(te));

        if (ConfigManager.showEnergyNetInfo)
            SEUtils.logInfo("Tileentity " + te + " has rejoined the energy network!", SEUtils.energyTile);
    }
    
	@Override
    public void attachGridObject(World world, int x, int y, int z, byte type) {
    	EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(world);
    	
    	EnergyNetAgent.getEnergyNetForWorld(world).addEvent(new GridEvent.AppendNode(world,x,y,z,type));
    	
    	/*if (energyNet.addGridNode(x, y, z, type)){
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("GridObject attached at " +String.valueOf(x)+":"+String.valueOf(y)+":"+String.valueOf(z));
    	}else{
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("Fail to attach gridObject at " +String.valueOf(x)+":"+String.valueOf(y)+":"+String.valueOf(z));
    	}*/
    }
    
	@Override
    public void detachGridObject(World world, int x, int y, int z) {
    	EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(world);

    	EnergyNetAgent.getEnergyNetForWorld(world).addEvent(new GridEvent.RemoveNode(world,x,y,z));
    	/*if (energyNet.removeGridNode(x, y, z)){
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("GridObject detached at "+String.valueOf(x)+":"+String.valueOf(y)+":"+String.valueOf(z));
    	}else{
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("Fail to detach gridObject at " +String.valueOf(x)+":"+String.valueOf(y)+":"+String.valueOf(z));
    	}*/
    }
    
	@Override
    public void connectGridNode(World world, int x1, int y1, int z1, int x2, int y2, int z2, double resistance) {
    	EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(world);

    	EnergyNetAgent.getEnergyNetForWorld(world).addEvent(new GridEvent.Connect(world,x1,y1,z1,x2,y2,z2,resistance));
    	/*if (energyNet.addGridConnection(x1, y1, z1, x2, y2, z2, resistance)){
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("Grid connection built between " +String.valueOf(x1)+":"+String.valueOf(y1)+":"+String.valueOf(z1)+" and "
					+String.valueOf(x2)+":"+String.valueOf(y2)+":"+String.valueOf(z2));
    	}else{
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("Fail to build grid connection between " +String.valueOf(x1)+":"+String.valueOf(y1)+":"+String.valueOf(z1)+" and "
					+String.valueOf(x2)+":"+String.valueOf(y2)+":"+String.valueOf(z2));
    	}*/
    }   
    
    public void breakGridConnection(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
    	EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(world);
    	
    	EnergyNetAgent.getEnergyNetForWorld(world).addEvent(new GridEvent.BreakConnection(world,x1,y1,z1,x2,y2,z2));
    	/*if (energyNet.removeGridConnection(x1, y1, z1, x2, y2, z2)){
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("Grid connection removed between " +String.valueOf(x1)+","+String.valueOf(y1)+","+String.valueOf(z1)+" and "
				+String.valueOf(x2)+","+String.valueOf(y2)+","+String.valueOf(z2));
    	}else{
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("Fail to remove grid connection between " +String.valueOf(x1)+","+String.valueOf(y1)+","+String.valueOf(z1)+" and "
				+String.valueOf(x2)+","+String.valueOf(y2)+","+String.valueOf(z2));
    	}*/
    }
}