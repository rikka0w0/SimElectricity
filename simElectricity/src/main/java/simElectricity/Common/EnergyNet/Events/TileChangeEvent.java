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

package simElectricity.Common.EnergyNet.Events;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.tileentity.TileEntity;

/**
 * Should be posted when a tileEntity has something changed, ie. output voltage, resistance...,
 * use {@link simElectricity.API.Energy#postTileChangeEvent(net.minecraft.tileentity.TileEntity)  SEEnergy.postTileChangeEvent()}
 */
public class TileChangeEvent extends Event {
    public TileEntity energyTile;

    public TileChangeEvent(TileEntity energyTile) {
        this.energyTile = energyTile;
    }
}
