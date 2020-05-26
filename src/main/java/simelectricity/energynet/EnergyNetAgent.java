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
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import simelectricity.api.components.*;
import simelectricity.api.internal.ISEEnergyNetAgent;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.node.ISESimulatable;
import simelectricity.api.node.ISESubComponent;
import simelectricity.api.tile.ISECableTile;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.api.tile.ISETile;
import simelectricity.api.tile.ISEWireTile;
import simelectricity.common.ConfigManager;
import simelectricity.common.SELogger;
import simelectricity.energynet.GridEvent.*;
import simelectricity.energynet.TileEvent.Attach;
import simelectricity.energynet.TileEvent.ConnectionChanged;
import simelectricity.energynet.TileEvent.Detach;
import simelectricity.energynet.TileEvent.ParamChanged;
import simelectricity.energynet.components.*;

import java.util.Map;
import java.util.WeakHashMap;

public class EnergyNetAgent implements ISEEnergyNetAgent {
    @SuppressWarnings("unchecked")
    public static Map<World, EnergyNet> mapping = new WeakHashMap();

    /**
     * Return the instance of energyNet for a specific world,
     * note that only server worlds can have energynet
     * <br>
     * If target not exist, it will automatically be created
     */
    public static EnergyNet getEnergyNetForWorld(World world) {
        if (!(world instanceof ServerWorld)) {
            throw new IllegalArgumentException("This world is not an instanceof WorldServer!");
        }

        EnergyNet ret = mapping.get(world);

        if (ret == null) {
            ret = new EnergyNet((ServerWorld) world);
            mapping.put(world, ret);
        }

        return ret;
    }

    public static void onWorldUnload(World world) {
        if (world.isRemote) {
            return;    //The energyNet is on server side, so ignore any client world!
        }

        EnergyNet energyNet = mapping.get(world);

        if (energyNet == null) {
            SELogger.logWarn(SELogger.energyNet, "Attempt to unload the EnergyNet associated with DIM" +
                    String.valueOf(world.dimension) + " but it does not exist!");
            return;
        }

        energyNet.notifyServerShuttingdown();
        mapping.remove(world);
    }

    public static boolean isNormalTile(TileEntity te) {
        return te instanceof ISETile || te instanceof ISECableTile || te instanceof ISEWireTile;
    }

    @Override
    public boolean canConnectTo(TileEntity tileEntity, Direction direction) {
        if (tileEntity instanceof ISECableTile) {
            ISECableTile cableTile = (ISECableTile) tileEntity;
            TileEntity neighborTileEntity = EnergyNetDataProvider.getTileEntityOnDirection(tileEntity, direction);


            if (!cableTile.canConnectOnSide(direction))
                return false;

            if (neighborTileEntity instanceof ISECableTile) {
                ISECableTile neighborCableTile = (ISECableTile) neighborTileEntity;
                return (
                        cableTile.getColor() == 0 ||
                                neighborCableTile.getColor() == 0 ||
                                cableTile.getColor() == neighborCableTile.getColor()
                ) &&     neighborCableTile.canConnectOnSide(direction.getOpposite());
            } else if (neighborTileEntity instanceof ISEWireTile) {
                return (((ISEWireTile) neighborTileEntity).getWireParam(direction.getOpposite())).hasBranchOnSide(null);
            } else if (neighborTileEntity instanceof ISETile) {
                return ((ISETile) neighborTileEntity).getComponent(direction.getOpposite()) != null;
            }
        } else if (tileEntity instanceof ISEWireTile) {
            if (!((ISEWireTile) tileEntity).getWireParam(direction).hasBranchOnSide(null))
                return false;

            TileEntity neighborTileEntity = EnergyNetDataProvider.getTileEntityOnDirection(tileEntity, direction);

            if (neighborTileEntity instanceof ISECableTile)
                return ((ISECableTile) neighborTileEntity).canConnectOnSide(direction.getOpposite());
            else if (neighborTileEntity instanceof ISETile)
                return ((ISETile) neighborTileEntity).getComponent(direction.getOpposite()) != null;
        } else if (tileEntity instanceof ISETile) {
            TileEntity neighborTileEntity = EnergyNetDataProvider.getTileEntityOnDirection(tileEntity, direction);

            if (neighborTileEntity instanceof ISECableTile)
                return  ((ISECableTile) neighborTileEntity).canConnectOnSide(direction.getOpposite());
            else if (neighborTileEntity instanceof ISEWireTile)
                return (((ISEWireTile) neighborTileEntity).getWireParam(direction.getOpposite())).hasBranchOnSide(null);
        } else {
            throw new RuntimeException("canConnectTo: input parameter \"tileEntity\" must implement either ISECableTile, ISEWireTile or ISETile");
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
        else if (dataProvider instanceof ISEConstantPowerSource)
            return new ConstantPowerSource((ISEConstantPowerSource) dataProvider, parent);
        else if (dataProvider instanceof ISEVoltageSource)
            return new VoltageSource((ISEVoltageSource) dataProvider, parent);
        else if (dataProvider instanceof ISESwitch)
            return new SwitchA((ISESwitch) dataProvider, parent);
        else if (dataProvider instanceof ISEWire && parent instanceof ISEWireTile)
            return new Wire((ISEWire)dataProvider, parent);
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
        return getEnergyNetForWorld(world).dataProvider.getGridObjectAtCoord(pos);
    }

    @Override
    public boolean isNodeValid(World world, ISESimulatable node) {
        return getEnergyNetForWorld(world).isNodeValid(node);
    }

    private boolean isInvalidTile(TileEntity te) {       
        if (!(isNormalTile(te) || te instanceof ISEGridTile)){
        	SELogger.logWarn(SELogger.energyNet, "Unknown tileentity " + te + " @["+te.getPos()+"], abort!");
        	return true;
        }

        if (te.getWorld().isRemote) {
            SELogger.logWarn(SELogger.energyNet,
                    "Client tileentity " + te + " @["+te.getPos()+"] attempt to call server-side API, abort!");
            throw new RuntimeException("Server-only API is called from client side!");
        }
        
        return false;
    }

    @Override
    public void attachTile(TileEntity te) {
        if (te.isRemoved()) {
            SELogger.logInfo(SELogger.energyNet, "Invalid tileentity " + te + " tried to attach, abort!");
            return;
        }
    	
        if (isInvalidTile(te))
        	return;

        if (isNormalTile(te)) {
            SELogger.logInfo(SELogger.energyNet, "Tileentity " + te + " attached to the EnergyNet");
        }

        if (te instanceof ISEGridTile) {
            SELogger.logInfo(SELogger.energyNet, "GridTile linked with GridNode at " + te.getPos());
        }

        getEnergyNetForWorld(te.getWorld()).addEvent(new Attach(te));
    }

    @Override
    public void updateTileParameter(TileEntity te) {
        if (isInvalidTile(te))
        	return;
        
        SELogger.logInfo(SELogger.energyNet, "Tileentity " + te + " requested for EnergyNet update");

        getEnergyNetForWorld(te.getWorld()).addEvent(new ParamChanged(te));
    }

    @Override
    public void detachTile(TileEntity te) {
        if (isInvalidTile(te))
        	return;

        if (te instanceof ISEGridTile)
            SELogger.logInfo(SELogger.energyNet, "GridTile invalidated at " + te.getPos());

        if (isNormalTile(te))
            SELogger.logInfo(SELogger.energyNet, "Tileentity " + te + " detached from the EnergyNet");

        getEnergyNetForWorld(te.getWorld()).addEvent(new Detach(te));
    }

    @Override
    public void updateTileConnection(TileEntity te) {
        if (isInvalidTile(te))
        	return;
        
        SELogger.logInfo(SELogger.energyNet, "Tileentity " + te + " updated its connection");

        getEnergyNetForWorld(te.getWorld()).addEvent(new ConnectionChanged(te));
    }


    @Override
    public void attachGridNode(World world, ISEGridNode node) {
        if (world.isRemote)
            throw new RuntimeException("Server-only API is called from client side!");
        
        SELogger.logInfo(SELogger.energyNet, "GridNode attached at " + node.getPos());

        getEnergyNetForWorld(world).addEvent(new AppendNode(node));
    }

    @Override
    public void detachGridNode(World world, ISEGridNode node) {
        if (world.isRemote)
            throw new RuntimeException("Server-only API is called from client side!");
        
        SELogger.logInfo(SELogger.energyNet, "GridNode detached at " + node.getPos());

        getEnergyNetForWorld(world).addEvent(new RemoveNode(node));
    }

    @Override
    public void connectGridNode(World world, ISEGridNode node1, ISEGridNode node2, double resistance) {
        if (world.isRemote)
            throw new RuntimeException("Server-only API is called from client side!");
        
        SELogger.logInfo(SELogger.energyNet, "Established connection between GridNodes: " + node1.getPos() + " and " + node2.getPos());

        getEnergyNetForWorld(world).addEvent(new Connect(node1, node2, resistance));
    }

    @Override
    public void breakGridConnection(World world, ISEGridNode node1, ISEGridNode node2) {
        if (world.isRemote)
            throw new RuntimeException("Server-only API is called from client side!");
        
        SELogger.logInfo(SELogger.energyNet, "Removed connection between GridNodes: " + node1.getPos() + " and " + node2.getPos());

        getEnergyNetForWorld(world).addEvent(new BreakConnection(node1, node2));
    }

    @Override
    public void makeTransformer(World world, ISEGridNode primary, ISEGridNode secondary, double resistance, double ratio) {
        if (world.isRemote)
            throw new RuntimeException("Server-only API is called from client side!");
        
        SELogger.logInfo(SELogger.energyNet, "New transformer, primary: " + primary.getPos() + ", secondary: " + secondary.getPos());

        getEnergyNetForWorld(world).addEvent(new MakeTransformer(primary, secondary, resistance, ratio));
    }

    @Override
    public void breakTransformer(World world, ISEGridNode node) {
        if (world.isRemote)
            throw new RuntimeException("Server-only API is called from client side!");
        
        SELogger.logInfo(SELogger.energyNet, "Removed transformer, winding: " + node.getPos());

        getEnergyNetForWorld(world).addEvent(new BreakTranformer(node));
    }

	@Override
	public double joule2rf() {
		return ConfigManager.joule2rf.get();
	}
}