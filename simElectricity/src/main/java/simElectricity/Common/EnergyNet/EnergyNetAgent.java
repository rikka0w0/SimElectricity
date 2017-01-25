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

import simElectricity.API.DataProvider.ISEComponentDataProvider;
import simElectricity.API.DataProvider.ISEConstantPowerLoadData;
import simElectricity.API.DataProvider.ISEDiodeData;
import simElectricity.API.DataProvider.ISEJunctionData;
import simElectricity.API.DataProvider.ISERegulatorData;
import simElectricity.API.DataProvider.ISETransformerData;
import simElectricity.API.DataProvider.ISEVoltageSourceData;
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.EnergyTile.ISESubComponent;
import simElectricity.Common.EnergyNet.Events.GridConnectionEvent;
import simElectricity.Common.EnergyNet.Events.GridDisconnectionEvent;
import simElectricity.Common.EnergyNet.Events.GridObjectAttachEvent;
import simElectricity.Common.EnergyNet.Events.GridObjectDetachEvent;
import simElectricity.Common.EnergyNet.Events.TileAttachEvent;
import simElectricity.Common.EnergyNet.Events.TileChangeEvent;
import simElectricity.Common.EnergyNet.Events.TileDetachEvent;
import simElectricity.Common.EnergyNet.Events.TileRejoinEvent;
import simElectricity.API.Internal.IEnergyNetAgent;
import simElectricity.API.Tile.ISECableTile;
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
        return EnergyNetAgent.getEnergyNetForWorld(obj.te.getWorldObj()).simulator.getVoltage(Tile);
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
        MinecraftForge.EVENT_BUS.post(new TileAttachEvent(te));
    }

	@Override
    public void markTileForUpdate(TileEntity te) {
        MinecraftForge.EVENT_BUS.post(new TileChangeEvent(te));
    }
	
	@Override
    public void detachTile(TileEntity te) {
        MinecraftForge.EVENT_BUS.post(new TileDetachEvent(te));
    }

	@Override
    public void reattachTile(TileEntity te) {
        MinecraftForge.EVENT_BUS.post(new TileRejoinEvent(te));
    }
    
	@Override
    public void attachGridObject(World world, int x, int y, int z, byte type) {
        MinecraftForge.EVENT_BUS.post(new GridObjectAttachEvent(world,x,y,z,type));
    }
    
	@Override
    public void detachGridObject(World world, int x, int y, int z) {
        MinecraftForge.EVENT_BUS.post(new GridObjectDetachEvent(world,x,y,z));
    }
    
	@Override
    public void connectGridNode(World world, int x1, int y1, int z1, int x2, int y2, int z2, double resistance) {
        MinecraftForge.EVENT_BUS.post(new GridConnectionEvent(world,x1,y1,z1,x2,y2,z2,resistance));
    }   
    
    public void breakGridConnection(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        MinecraftForge.EVENT_BUS.post(new GridDisconnectionEvent(world,x1,y1,z1,x2,y2,z2));
    }
}