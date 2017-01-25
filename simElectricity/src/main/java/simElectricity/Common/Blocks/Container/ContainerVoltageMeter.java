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

package simElectricity.Common.Blocks.Container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import simElectricity.API.Common.ContainerBase;
import simElectricity.API.SEAPI;
import simElectricity.Common.Blocks.TileEntity.TileVoltageMeter;

public class ContainerVoltageMeter extends ContainerBase {
    public ContainerVoltageMeter(InventoryPlayer inventoryPlayer, TileEntity te) {
        super(inventoryPlayer, te);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        ((TileVoltageMeter) tileEntity).voltage = SEAPI.energyNetAgent.getVoltage(((TileVoltageMeter) tileEntity).tile);
        SEAPI.networkManager.updateTileEntityFields(tileEntity, "voltage");
    }

    @Override
    public int getPlayerInventoryStartIndex() {
        return 27;
    }

    @Override
    public int getPlayerInventoryEndIndex() {
        return 36;
    }

    @Override
    public int getTileInventoryStartIndex() {
        return 0;
    }

    @Override
    public int getTileInventoryEndIndex() {
        return 27;
    }
}
