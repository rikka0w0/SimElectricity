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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import simelectricity.api.components.*;
import simelectricity.api.internal.IEnergyNetAgent;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.node.ISESimulatable;
import simelectricity.api.node.ISESubComponent;
import simelectricity.api.tile.ISECableTile;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.api.tile.ISETile;
import simelectricity.common.SELogger;
import simelectricity.energynet.GridEvent.AppendNode;
import simelectricity.energynet.GridEvent.BreakConnection;
import simelectricity.energynet.GridEvent.BreakTranformer;
import simelectricity.energynet.GridEvent.Connect;
import simelectricity.energynet.GridEvent.MakeTransformer;
import simelectricity.energynet.GridEvent.RemoveNode;
import simelectricity.energynet.TileEvent.Attach;
import simelectricity.energynet.TileEvent.ConnectionChanged;
import simelectricity.energynet.TileEvent.Detach;
import simelectricity.energynet.TileEvent.ParamChanged;
import simelectricity.energynet.components.*;

import java.util.Map;
import java.util.WeakHashMap;

public class EnergyNetAgent implements IEnergyNetAgent {
    @SuppressWarnings("unchecked")
    public static Map<World, EnergyNet> mapping = new WeakHashMap();

    /**
     * Return the instance of energyNet for a specific world,
     * note that only server worlds can have energynet
     * <p/>
     * If target not exist, it will automatically be created
     */
    public static EnergyNet getEnergyNetForWorld(World world) {
        if (!(world instanceof WorldServer))
            throw new IllegalArgumentException("worlid is not an instanceof WorldServer!");

        EnergyNet ret = EnergyNetAgent.mapping.get(world);

        if (ret == null) {
            ret = new EnergyNet((WorldServer) world);
            EnergyNetAgent.mapping.put(world, ret);
        }

        return ret;
    }

    public static void onWorldUnload(World world) {
        if (world.isRemote)
            return;    //The energyNet is on server side, so ignore any client world!

        EnergyNetAgent.mapping.get(world).notifyServerShuttingdown();
        EnergyNetAgent.mapping.remove(world);
    }

    @Override
    public double getVoltage(ISESimulatable Tile) {
        SEComponent obj = (SEComponent) Tile;
        EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(obj.te.getWorld());

        return EnergyNet.getVoltage(Tile);
    }

    @Override
    public double getCurrentMagnitude(ISESimulatable Tile) {
        SEComponent obj = (SEComponent) Tile;
        EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(obj.te.getWorld());

        return EnergyNetAgent.getEnergyNetForWorld(obj.te.getWorld()).getCurrentMagnitude(Tile);
    }

    @Override
    public boolean canConnectTo(TileEntity tileEntity, EnumFacing direction) {
        if (tileEntity instanceof ISECableTile) {
            ISECableTile cableTile = (ISECableTile) tileEntity;
            TileEntity neighborTileEntity = EnergyNetDataProvider.getTileEntityOnDirection(tileEntity, direction);

            if (neighborTileEntity instanceof ISECableTile) {
                ISECableTile neighborCableTile = (ISECableTile) neighborTileEntity;
                return (
                        cableTile.getColor() == 0 ||
                                neighborCableTile.getColor() == 0 ||
                                cableTile.getColor() == neighborCableTile.getColor()
                ) && cableTile.canConnectOnSide(direction) &&
                        neighborCableTile.canConnectOnSide(direction.getOpposite());
            } else if (neighborTileEntity instanceof ISETile) {
                return ((ISETile) neighborTileEntity).getComponent(direction.getOpposite()) != null;
            }
        } else if (tileEntity instanceof ISETile) {
            ISETile tile = (ISETile) tileEntity;
            TileEntity neighborTileEntity = EnergyNetDataProvider.getTileEntityOnDirection(tileEntity, direction);

            if (neighborTileEntity instanceof ISECableTile)
                return ((Cable) ((ISECableTile) neighborTileEntity).getNode()).canConnectOnSide(direction.getOpposite());
        } else {
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
            return new SwitchA((ISESwitch) dataProvider, parent);
        return null;
    }

    @Override
    public ISESimulatable newCable(TileEntity dataProviderTileEntity, boolean isGridInterConnectionPoint) {
        if (dataProviderTileEntity instanceof ISECableTile)
            return new Cable((ISECableTile) dataProviderTileEntity, dataProviderTileEntity, isGridInterConnectionPoint);
        return null;
    }

    @Override
    public ISEGridNode newGridNode(BlockPos pos, int numOfParallelConductor) {
        return new GridNode(pos, (byte) numOfParallelConductor);
    }

    @Override
    public ISEGridNode getGridNodeAt(World world, BlockPos pos) {
        EnergyNetAgent.getEnergyNetForWorld(world).dataProvider.getGridObjectAtCoord(pos);
        return null;
    }

    @Override
    public boolean isNodeValid(World world, ISESimulatable node) {
        return EnergyNetAgent.getEnergyNetForWorld(world).isNodeValid(node);
    }

    private boolean isInvalidTile(TileEntity te) {       
        if (!(te instanceof ISETile || te instanceof ISECableTile || te instanceof ISEGridTile)){
        	SELogger.logWarn(SELogger.energyNet, "Unknown tileentity " + te + ", aborted");
        	return true;
        }

        if (te.getWorld().isRemote) {
            SELogger.logWarn(SELogger.energyNet, "Client tileentity " + te + " is found, aborted");
            throw new RuntimeException("Server-only API is called from client side!");
        }
        
        return false;
    }

    @Override
    public void attachTile(TileEntity te) {
        if (te.isInvalid()) {
            SELogger.logInfo(SELogger.energyNet, "Invalid tileentity " + te + " is trying to attach, aborted");
            return;
        }
    	
        if (isInvalidTile(te))
        	return;

        if (te instanceof ISETile || te instanceof ISECableTile) {
            SELogger.logInfo(SELogger.energyNet, "Tileentity " + te + " attached to the EnergyNet");
        }

        if (te instanceof ISEGridTile) {
            SELogger.logInfo(SELogger.energyNet, "GridTile linked with GridNode at " + te.getPos());
        }
        
        EnergyNetAgent.getEnergyNetForWorld(te.getWorld()).addEvent(new Attach(te));
    }

    @Override
    public void updateTileParameter(TileEntity te) {
        if (isInvalidTile(te))
        	return;
        
        SELogger.logInfo(SELogger.energyNet, "Tileentity " + te + " requested for EnergyNet update");
        
        EnergyNetAgent.getEnergyNetForWorld(te.getWorld()).addEvent(new ParamChanged(te));
    }

    @Override
    public void detachTile(TileEntity te) {
        if (isInvalidTile(te))
        	return;

        if (te instanceof ISEGridTile)
            SELogger.logInfo(SELogger.energyNet, "GridTile invalidated at " + te.getPos());

        if (te instanceof ISETile || te instanceof ISECableTile)
                SELogger.logInfo(SELogger.energyNet, "Tileentity " + te + " detached from the EnergyNet");
        
        EnergyNetAgent.getEnergyNetForWorld(te.getWorld()).addEvent(new Detach(te));
    }

    @Override
    public void updateTileConnection(TileEntity te) {
        if (isInvalidTile(te))
        	return;
        
        SELogger.logInfo(SELogger.energyNet, "Tileentity " + te + " updated its connection");
        
        EnergyNetAgent.getEnergyNetForWorld(te.getWorld()).addEvent(new ConnectionChanged(te));
    }


    @Override
    public void attachGridNode(World world, ISEGridNode node) {
        if (world.isRemote)
            throw new RuntimeException("Server-only API is called from client side!");
        
        SELogger.logInfo(SELogger.energyNet, "GridNode attached at " + node.getPos());
        
        EnergyNetAgent.getEnergyNetForWorld(world).addEvent(new AppendNode(node));
    }

    @Override
    public void detachGridNode(World world, ISEGridNode node) {
        if (world.isRemote)
            throw new RuntimeException("Server-only API is called from client side!");
        
        SELogger.logInfo(SELogger.energyNet, "GridNode detached at " + node.getPos());
        
        EnergyNetAgent.getEnergyNetForWorld(world).addEvent(new RemoveNode(node));
    }

    @Override
    public void connectGridNode(World world, ISEGridNode node1, ISEGridNode node2, double resistance) {
        if (world.isRemote)
            throw new RuntimeException("Server-only API is called from client side!");
        
        SELogger.logInfo(SELogger.energyNet, "Established connection between GridNodes: " + node1.getPos() + " and " + node2.getPos());
        
        EnergyNetAgent.getEnergyNetForWorld(world).addEvent(new Connect(node1, node2, resistance));
    }

    @Override
    public void breakGridConnection(World world, ISEGridNode node1, ISEGridNode node2) {
        if (world.isRemote)
            throw new RuntimeException("Server-only API is called from client side!");
        
        SELogger.logInfo(SELogger.energyNet, "Removed connection between GridNodes: " + node1.getPos() + " and " + node2.getPos());
        
        EnergyNetAgent.getEnergyNetForWorld(world).addEvent(new BreakConnection(node1, node2));
    }

    @Override
    public void makeTransformer(World world, ISEGridNode primary, ISEGridNode secondary, double resistance, double ratio) {
        if (world.isRemote)
            throw new RuntimeException("Server-only API is called from client side!");
        
        SELogger.logInfo(SELogger.energyNet, "New transformer, primary: " + primary.getPos() + ", secondary: " + secondary.getPos());
        
        EnergyNetAgent.getEnergyNetForWorld(world).addEvent(new MakeTransformer(primary, secondary, resistance, ratio));
    }

    @Override
    public void breakTransformer(World world, ISEGridNode node) {
        if (world.isRemote)
            throw new RuntimeException("Server-only API is called from client side!");
        
        SELogger.logInfo(SELogger.energyNet, "Removed transformer, winding: " + node.getPos());
        
        EnergyNetAgent.getEnergyNetForWorld(world).addEvent(new BreakTranformer(node));
    }
}