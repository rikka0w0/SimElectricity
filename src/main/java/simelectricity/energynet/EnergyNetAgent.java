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

package simelectricity.energynet;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Map;
import java.util.WeakHashMap;

import simelectricity.api.components.ISEComponentParameter;
import simelectricity.api.components.ISEConstantPowerLoad;
import simelectricity.api.components.ISEDiode;
import simelectricity.api.components.ISESwitch;
import simelectricity.api.components.ISETransformer;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.node.ISESimulatable;
import simelectricity.api.node.ISESubComponent;

import simelectricity.common.ConfigManager;
import simelectricity.common.SEUtils;
import simelectricity.api.internal.IEnergyNetAgent;
import simelectricity.api.tile.ISECableTile;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.api.tile.ISETile;
import simelectricity.energynet.components.Cable;
import simelectricity.energynet.components.ConstantPowerLoad;
import simelectricity.energynet.components.DiodeInput;
import simelectricity.energynet.components.GridNode;
import simelectricity.energynet.components.SEComponent;
import simelectricity.energynet.components.SwitchA;
import simelectricity.energynet.components.TransformerPrimary;
import simelectricity.energynet.components.VoltageSource;

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
    	if (world.isRemote)
    		return;	//The energyNet is on server side, so ignore any client world!
    	
    	mapping.get(world).notifyServerShuttingdown();
        mapping.remove(world);
    }
    
    @Override
    public double getVoltage(ISESimulatable Tile) {
    	SEComponent obj = (SEComponent) Tile;
    	EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(obj.te.getWorldObj());
    	
        return energyNet.getVoltage(Tile);
    }
    
    @Override
    public double getCurrentMagnitude(ISESimulatable Tile){
    	SEComponent obj = (SEComponent) Tile;
    	EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(obj.te.getWorldObj());
    	
        return EnergyNetAgent.getEnergyNetForWorld(obj.te.getWorldObj()).getCurrentMagnitude(Tile);
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
	public ISESubComponent newComponent(ISEComponentParameter dataProvider, TileEntity parent) {
		if (dataProvider instanceof ISEDiode)
			//Create a pair of DiodeInput and DiodeOutput at the same time
			return new DiodeInput((ISEDiode) dataProvider, parent);
		else if (dataProvider instanceof ISETransformer)
			return new TransformerPrimary((ISETransformer) dataProvider, parent);
		else if (dataProvider instanceof ISEConstantPowerLoad)
			return new ConstantPowerLoad((ISEConstantPowerLoad) dataProvider, parent);
		else if (dataProvider instanceof ISEVoltageSource)
			return new VoltageSource((ISEVoltageSource) dataProvider, parent);
		else if (dataProvider instanceof ISESwitch)
			return new SwitchA(((ISESwitch)dataProvider), parent);
		return null;
	}
	
	@Override
	public ISESimulatable newCable(TileEntity dataProviderTileEntity, boolean isGridInterConnectionPoint){
		if (dataProviderTileEntity instanceof ISECableTile)
			return new Cable((ISECableTile) dataProviderTileEntity, dataProviderTileEntity, isGridInterConnectionPoint);
		return null;
	}

	@Override
	public ISEGridNode newGridNode(int x, int y, int z){
		return new GridNode(x, y, z);
	}
	
	@Override
	public ISEGridNode getGridNodeAt(World world, int x, int y, int z) {
		EnergyNetAgent.getEnergyNetForWorld(world).dataProvider.getGridObjectAtCoord(x, y, z);
		return null;
	}
	
	@Override
	public boolean isNodeValid(World world, ISESimulatable node) {
		return EnergyNetAgent.getEnergyNetForWorld(world).isNodeValid(node);
	}
	
	
	@Override
    public void attachTile(TileEntity te) {
        if (!te.getWorldObj().blockExists(te.xCoord, te.yCoord, te.zCoord)) {
            SEUtils.logInfo(te + " is added to the energy net too early!, abort!", SEUtils.energyNet);
            return;
        }

        if (te.isInvalid()) {
            SEUtils.logInfo("Invalid tileentity " + te + " is trying to attach to the energy network, aborting", SEUtils.energyNet);
            return;
        }
        
        if (te.getWorldObj().isRemote) {
            SEUtils.logInfo("Client tileentity " + te + " is requesting, abort!", SEUtils.energyNet);
            return;
        }
        
        World world = te.getWorldObj();
        EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(world);
        if (te instanceof ISEGridTile) {
        	energyNet.addEvent(new TileEvent.GridTilePresent(te));
        	
        	if (ConfigManager.showEnergyNetInfo)
        		SEUtils.logInfo("GridTile linked with GridObject at "+String.valueOf(te.xCoord)+","+String.valueOf(te.yCoord)+","+String.valueOf(te.zCoord), SEUtils.energyNet);
        }
        
        if (te instanceof ISETile || te instanceof ISECableTile) {
	        energyNet.addEvent(new TileEvent.Attach(te));
	
	        if (ConfigManager.showEnergyNetInfo)
	            SEUtils.logInfo("Tileentity " + te + " has attached to the energy network!", SEUtils.energyNet);        
        }
    }

	@Override
    public void updateTileParameter(TileEntity te) {
        if (te.getWorldObj().isRemote) {
            SEUtils.logInfo("Client tileentity " + te + " is requesting, aborting", SEUtils.energyNet);
            return;
        }

        EnergyNetAgent.getEnergyNetForWorld(te.getWorldObj()).addEvent(new TileEvent.ParamChanged(te));

        if (ConfigManager.showEnergyNetInfo)
            SEUtils.logInfo("Tileentity " + te + " causes the energy network to update!", SEUtils.energyNet);
    }
	
	@Override
    public void detachTile(TileEntity te) {
        if (te.getWorldObj().isRemote) {
            SEUtils.logInfo("Client tileentity " + te + " is requesting, abort!", SEUtils.energyNet);
            return;
        }

        World world = te.getWorldObj();
        EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(world);
        if (te instanceof ISEGridTile) {
        	energyNet.addEvent(new TileEvent.GridTileInvalidate(te)); 
        	
        	if (ConfigManager.showEnergyNetInfo)
        		SEUtils.logInfo("GridTile destroyed at"+String.valueOf(te.xCoord)+","+String.valueOf(te.yCoord)+","+String.valueOf(te.zCoord), SEUtils.energyNet);
        }
        
        if (te instanceof ISETile || te instanceof ISECableTile) {
	        energyNet.addEvent(new TileEvent.Detach(te));     	
	        if (ConfigManager.showEnergyNetInfo)
	            SEUtils.logInfo("Tileentity " + te + " has detached from the energy network!", SEUtils.energyNet);
        }
    }

	@Override
    public void updateTileConnection(TileEntity te) {
        if (te.getWorldObj().isRemote) {
        	throw new RuntimeException("Server-only API is called from client side!");
        }

        EnergyNetAgent.getEnergyNetForWorld(te.getWorldObj()).addEvent(new TileEvent.ConnectionChanged(te));

        if (ConfigManager.showEnergyNetInfo)
            SEUtils.logInfo("Tileentity " + te + " has rejoined the energy network!", SEUtils.energyNet);
    }
    
	
	
	
	@Override
    public void attachGridObject(World world, ISEGridNode node) {
    	EnergyNetAgent.getEnergyNetForWorld(world).addEvent(new GridEvent.AppendNode(world, node));
    	
    	/*if (energyNet.addGridNode(x, y, z, type)){
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("GridObject attached at " +String.valueOf(x)+":"+String.valueOf(y)+":"+String.valueOf(z));
    	}else{
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("Fail to attach gridObject at " +String.valueOf(x)+":"+String.valueOf(y)+":"+String.valueOf(z));
    	}*/
    }
    
	@Override
    public void detachGridObject(World world, ISEGridNode node) {
    	EnergyNetAgent.getEnergyNetForWorld(world).addEvent(new GridEvent.RemoveNode(world, node));
    	/*if (energyNet.removeGridNode(x, y, z)){
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("GridObject detached at "+String.valueOf(x)+":"+String.valueOf(y)+":"+String.valueOf(z));
    	}else{
    		if (ConfigManager.showEnergyNetInfo)
    			SEUtils.logInfo("Fail to detach gridObject at " +String.valueOf(x)+":"+String.valueOf(y)+":"+String.valueOf(z));
    	}*/
    }
    
	@Override
    public void connectGridNode(World world, ISEGridNode node1, ISEGridNode node2, double resistance) {
    	EnergyNetAgent.getEnergyNetForWorld(world).addEvent(new GridEvent.Connect(world, node1, node2, resistance));
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
    
    @Override
	public void breakGridConnection(World world, ISEGridNode node1, ISEGridNode node2) {
    	EnergyNetAgent.getEnergyNetForWorld(world).addEvent(new GridEvent.BreakConnection(world, node1, node2));
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

	@Override
	public void makeTransformer(World world, ISEGridNode primary, ISEGridNode secondary, double resistance, double ratio) {
		EnergyNetAgent.getEnergyNetForWorld(world).addEvent(new GridEvent.MakeTransformer(world, primary, secondary, resistance, ratio));
	}

	@Override
	public void breakTransformer(World world, ISEGridNode node) {
		EnergyNetAgent.getEnergyNetForWorld(world).addEvent(new GridEvent.BreakTranformer(world, node));
	}
}