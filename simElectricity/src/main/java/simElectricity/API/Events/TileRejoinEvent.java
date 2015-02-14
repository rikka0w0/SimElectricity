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

package simElectricity.API.Events;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Should be posted when a tileEntity is rejoined to the energy net,
 * use {@link simElectricity.API.Energy#postTileRejoinEvent(net.minecraft.tileentity.TileEntity) Energy.postTileRejoinEvent()}
 */
public class TileRejoinEvent extends Event {
    public TileEntity energyTile;

    public TileRejoinEvent(TileEntity energyTile) {
        this.energyTile = energyTile;
    }
}