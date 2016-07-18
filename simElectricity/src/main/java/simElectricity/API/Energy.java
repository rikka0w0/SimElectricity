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

import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.Events.*;
import simElectricity.Common.EnergyNet.EnergyNet;

/**
 * Energy net
 */
public class Energy {
	public static double convertSE2IC(double SEPower){
		return SEPower/10;
	}

	public static double convertIC2SE(double ICPower){
		return ICPower*10;
	}
	
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
     * Calculate the voltage of a given EnergyTile RELATIVE TO GROUND!
     * For {@link simElectricity.API.EnergyTile.IEnergyTile} and {@link simElectricity.API.EnergyTile.IConductor} Only!
     */
    public static double getVoltage(TileEntity Tile) {
        return getVoltage((ISESimulatable)Tile, Tile.getWorldObj());
    }
    
    
    /**
     * Calculate the voltage of a given EnergyTile RELATIVE TO GROUND!
     */
    
    public static double getVoltage(ISESimulatable Tile, World world) {
        return EnergyNet.getVoltage(Tile, world);
    }
    
    public static void postGridObjectAttachEvent(World world, int x, int y, int z, byte type) {
        MinecraftForge.EVENT_BUS.post(new GridObjectAttachEvent(world,x,y,z,type));
    }
    
    public static void postGridObjectDetachEvent(World world, int x, int y, int z) {
        MinecraftForge.EVENT_BUS.post(new GridObjectDetachEvent(world,x,y,z));
    }
    
    public static void postGridConnectionEvent(World world, int x1, int y1, int z1, int x2, int y2, int z2, double resistance) {
        MinecraftForge.EVENT_BUS.post(new GridConnectionEvent(world,x1,y1,z1,x2,y2,z2,resistance));
    }   
    
    public static void postGridDisconnectionEvent(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        MinecraftForge.EVENT_BUS.post(new GridDisconnectionEvent(world,x1,y1,z1,x2,y2,z2));
    }
}
