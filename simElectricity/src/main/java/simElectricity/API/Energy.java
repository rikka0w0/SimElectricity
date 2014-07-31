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

package simElectricity.API;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import simElectricity.API.EnergyTile.IBaseComponent;
import simElectricity.API.EnergyTile.ICircuitComponent;
import simElectricity.API.EnergyTile.IEnergyTile;
import simElectricity.API.EnergyTile.ITransformer.ITransformerWinding;
import simElectricity.API.Events.TileAttachEvent;
import simElectricity.API.Events.TileChangeEvent;
import simElectricity.API.Events.TileDetachEvent;
import simElectricity.API.Events.TileRejoinEvent;
import simElectricity.Common.EnergyNet.EnergyNet;

/**
 * Energy net
 */
public class Energy {

    /**
     * Post a {@link simElectricity.API.Events.TileAttachEvent} for a tileEntity
     */
    public static void postTileAttachEvent(TileEntity te) {
        MinecraftForge.EVENT_BUS.post(new TileAttachEvent(te));
    }

    /**
     * Post a {@link simElectricity.API.Events.TileChangeEvent} for a tileEntity
     */
    public static void postTileChangeEvent(TileEntity te) {
        MinecraftForge.EVENT_BUS.post(new TileChangeEvent(te));
    }

    /**
     * Post a {@link simElectricity.API.Events.TileDetachEvent} for a tileEntity
     */
    public static void postTileDetachEvent(TileEntity te) {
        MinecraftForge.EVENT_BUS.post(new TileDetachEvent(te));
    }

    /**
     * Post a {@link simElectricity.API.Events.TileRejoinEvent} for a tileEntity
     */
    public static void postTileRejoinEvent(TileEntity te) {
        MinecraftForge.EVENT_BUS.post(new TileRejoinEvent(te));
    }

    /**
     * Calculate the energy output from a {@link simElectricity.API.EnergyTile.IEnergyTile} in one tick (1/20 second)
     * For {@link simElectricity.API.EnergyTile.IEnergyTile} Only!
     */
    public static float getWorkDonePerTick(IEnergyTile Tile) {
        return getWorkDonePerTick(Tile, ((TileEntity) Tile).getWorldObj());
    }

    /**
     * Calculate the consumed power for a given EnergyTile
     * For {@link simElectricity.API.EnergyTile.IEnergyTile} and {@link simElectricity.API.EnergyTile.IConductor} Only!
     */
    public static float getPower(IEnergyTile Tile) {
        return getPower(Tile, ((TileEntity) Tile).getWorldObj());
    }

    /**
     * Calculate the input/output for a given EnergyTile
     * For {@link simElectricity.API.EnergyTile.IEnergyTile} and {@link simElectricity.API.EnergyTile.IConductor} Only!
     */
    public static float getCurrent(IEnergyTile Tile) {
        return getCurrent(Tile, ((TileEntity) Tile).getWorldObj());
    }

    /**
     * Calculate the voltage of a given EnergyTile RELATIVE TO GROUND!
     * For {@link simElectricity.API.EnergyTile.IEnergyTile} and {@link simElectricity.API.EnergyTile.IConductor} Only!
     */
    public static float getVoltage(IBaseComponent Tile) {
        if (Tile instanceof ITransformerWinding) {
            return getVoltage(Tile, ((TileEntity) (((ITransformerWinding) Tile).getCore())).getWorldObj());
        } else {
            return getVoltage(Tile, ((TileEntity) Tile).getWorldObj());
        }
    }

    /**
     * Calculate the energy output from a {@link simElectricity.API.EnergyTile.IEnergyTile} in one tick (1/20 second)
     */
    public static float getWorkDonePerTick(ICircuitComponent Tile, World world) {
        if (Tile.getOutputVoltage() > 0) {//Energy Source
            return (float) (0.05 * getVoltage(Tile, world) * getCurrent(Tile, world));
        } else {//Energy Sink
            return 0;
        }
    }

    /**
     * Calculate the consumed power for a given EnergyTile
     */
    public static float getPower(ICircuitComponent Tile, World world) {
        if (Tile.getOutputVoltage() > 0) {//Energy Source
            return ((Tile.getOutputVoltage() - getVoltage(Tile, world)) * (Tile.getOutputVoltage() - getVoltage(Tile, world))) / Tile.getResistance();
        } else {//Energy Sink
            return getVoltage(Tile, world) * getVoltage(Tile, world) / Tile.getResistance();
        }
    }

    /**
     * Calculate the input/output for a given EnergyTile
     */
    public static float getCurrent(ICircuitComponent Tile, World world) {
        if (Tile.getOutputVoltage() > 0) {            //Energy Source
            return (Tile.getOutputVoltage() - getVoltage(Tile, world)) / Tile.getResistance();
        } else {                                    //Energy Sink
            return getVoltage(Tile, world) / Tile.getResistance();
        }
    }

    /**
     * Calculate the voltage of a given EnergyTile RELATIVE TO GROUND!
     */
    public static float getVoltage(IBaseComponent Tile, World world) {
        if (EnergyNet.getForWorld(world).voltageCache.containsKey(Tile))
            return EnergyNet.getForWorld(world).voltageCache.get(Tile);
        else
            return 0;
    }
}
