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

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
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
    public static Map<Level, EnergyNet> mapping = new WeakHashMap<>();

    /**
     * Return the instance of energyNet for a specific world,
     * note that only server worlds can have energynet
     * <br>
     * If target not exist, it will automatically be created
     */
    public static EnergyNet getEnergyNetForWorld(Level world) {
        if (!(world instanceof ServerLevel)) {
            throw new IllegalArgumentException("This world is not an instanceof WorldServer!");
        }

        EnergyNet ret = mapping.get(world);

        if (ret == null) {
            ret = new EnergyNet((ServerLevel) world);
            mapping.put(world, ret);
        }

        return ret;
    }

    public static void onWorldUnload(Level world) {
        if (world.isClientSide) {
            return;    //The energyNet is on server side, so ignore any client world!
        }

        EnergyNet energyNet = mapping.get(world);

        if (energyNet == null) {
            SELogger.logWarn(SELogger.energyNet, "Attempt to unload the EnergyNet associated with DIM" +
                    String.valueOf(world.dimension().getRegistryName()) + " but it does not exist!");
            return;
        }

        energyNet.notifyServerShuttingdown();
        mapping.remove(world);
    }

    public static boolean isNormalTile(BlockEntity te) {
        return te instanceof ISETile || te instanceof ISECableTile || te instanceof ISEWireTile;
    }

    @Override
    public boolean canConnectTo(BlockEntity tileEntity, Direction direction) {
        if (tileEntity instanceof ISECableTile) {
            ISECableTile cableTile = (ISECableTile) tileEntity;
            BlockEntity neighborTileEntity = EnergyNetDataProvider.getTileEntityOnDirection(tileEntity, direction);


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

            BlockEntity neighborTileEntity = EnergyNetDataProvider.getTileEntityOnDirection(tileEntity, direction);

            if (neighborTileEntity instanceof ISECableTile)
                return ((ISECableTile) neighborTileEntity).canConnectOnSide(direction.getOpposite());
            else if (neighborTileEntity instanceof ISETile)
                return ((ISETile) neighborTileEntity).getComponent(direction.getOpposite()) != null;
        } else if (tileEntity instanceof ISETile) {
            BlockEntity neighborTileEntity = EnergyNetDataProvider.getTileEntityOnDirection(tileEntity, direction);

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
    public ISESubComponent<?> newComponent(ISEComponentParameter dataProvider, BlockEntity parent) {
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
    public ISESimulatable newCable(BlockEntity dataProviderTileEntity, boolean isGridInterConnectionPoint) {
        if (dataProviderTileEntity instanceof ISECableTile)
            return new Cable((ISECableTile) dataProviderTileEntity, dataProviderTileEntity, isGridInterConnectionPoint);
        return null;
    }

    @Override
    public ISEGridNode newGridNode(BlockPos pos, int numOfParallelConductor) {
        return new GridNode(pos, (byte) numOfParallelConductor);
    }

    @Override
    public ISEGridNode getGridNodeAt(Level world, BlockPos pos) {
        return getEnergyNetForWorld(world).dataProvider.getGridObjectAtCoord(pos);
    }

    @Override
    public boolean isNodeValid(Level world, ISESimulatable node) {
        return getEnergyNetForWorld(world).isNodeValid(node);
    }

    private boolean isInvalidTile(BlockEntity te) {
        if (!(isNormalTile(te) || te instanceof ISEGridTile)){
        	SELogger.logWarn(SELogger.energyNet, "Unknown tileentity " + te + " @["+te.getBlockPos()+"], abort!");
        	return true;
        }

        if (te.getLevel().isClientSide) {
            SELogger.logWarn(SELogger.energyNet,
                    "Client tileentity " + te + " @["+te.getBlockPos()+"] attempt to call server-side API, abort!");
            throw new RuntimeException("Server-only API is called from client side!");
        }

        return false;
    }

    @Override
    public void attachTile(BlockEntity te) {
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
            SELogger.logInfo(SELogger.energyNet, "GridTile linked with GridNode at " + te.getBlockPos());
        }

        getEnergyNetForWorld(te.getLevel()).addEvent(new Attach(te));
    }

    @Override
    public void updateTileParameter(BlockEntity te) {
        if (isInvalidTile(te))
        	return;

        SELogger.logInfo(SELogger.energyNet, "Tileentity " + te + " requested for EnergyNet update");

        getEnergyNetForWorld(te.getLevel()).addEvent(new ParamChanged(te));
    }

    @Override
    public void detachTile(BlockEntity te) {
        if (isInvalidTile(te))
        	return;

        if (te instanceof ISEGridTile)
            SELogger.logInfo(SELogger.energyNet, "GridTile invalidated at " + te.getBlockPos());

        if (isNormalTile(te))
            SELogger.logInfo(SELogger.energyNet, "Tileentity " + te + " detached from the EnergyNet");

        getEnergyNetForWorld(te.getLevel()).addEvent(new Detach(te));
    }

    @Override
    public void updateTileConnection(BlockEntity te) {
        if (isInvalidTile(te))
        	return;

        SELogger.logInfo(SELogger.energyNet, "Tileentity " + te + " updated its connection");

        getEnergyNetForWorld(te.getLevel()).addEvent(new ConnectionChanged(te));
    }


    @Override
    public void attachGridNode(Level world, ISEGridNode node) {
        if (world.isClientSide)
            throw new RuntimeException("Server-only API is called from client side!");

        SELogger.logInfo(SELogger.energyNet, "GridNode attached at " + node.getPos());

        getEnergyNetForWorld(world).addEvent(new AppendNode(node));
    }

    @Override
    public void detachGridNode(Level world, ISEGridNode node) {
        if (world.isClientSide)
            throw new RuntimeException("Server-only API is called from client side!");

        SELogger.logInfo(SELogger.energyNet, "GridNode detached at " + node.getPos());

        getEnergyNetForWorld(world).addEvent(new RemoveNode(node));
    }

    @Override
    public void connectGridNode(Level world, ISEGridNode node1, ISEGridNode node2, double resistance) {
        if (world.isClientSide)
            throw new RuntimeException("Server-only API is called from client side!");

        SELogger.logInfo(SELogger.energyNet, "Established connection between GridNodes: " + node1.getPos() + " and " + node2.getPos());

        getEnergyNetForWorld(world).addEvent(new Connect(node1, node2, resistance));
    }

    @Override
    public void breakGridConnection(Level world, ISEGridNode node1, ISEGridNode node2) {
        if (world.isClientSide)
            throw new RuntimeException("Server-only API is called from client side!");

        SELogger.logInfo(SELogger.energyNet, "Removed connection between GridNodes: " + node1.getPos() + " and " + node2.getPos());

        getEnergyNetForWorld(world).addEvent(new BreakConnection(node1, node2));
    }

    @Override
    public void makeTransformer(Level world, ISEGridNode primary, ISEGridNode secondary, double resistance, double ratio) {
        if (world.isClientSide)
            throw new RuntimeException("Server-only API is called from client side!");

        SELogger.logInfo(SELogger.energyNet, "New transformer, primary: " + primary.getPos() + ", secondary: " + secondary.getPos());

        getEnergyNetForWorld(world).addEvent(new MakeTransformer(primary, secondary, resistance, ratio));
    }

    @Override
    public void breakTransformer(Level world, ISEGridNode node) {
        if (world.isClientSide)
            throw new RuntimeException("Server-only API is called from client side!");

        SELogger.logInfo(SELogger.energyNet, "Removed transformer, winding: " + node.getPos());

        getEnergyNetForWorld(world).addEvent(new BreakTranformer(node));
    }

	@Override
	public double joule2rf() {
		return ConfigManager.joule2rf.get();
	}
}