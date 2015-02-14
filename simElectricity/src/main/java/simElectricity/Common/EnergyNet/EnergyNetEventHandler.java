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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import simElectricity.API.EnergyTile.IBaseComponent;
import simElectricity.API.EnergyTile.IComplexTile;
import simElectricity.API.EnergyTile.ITransformer;
import simElectricity.API.Events.TileAttachEvent;
import simElectricity.API.Events.TileChangeEvent;
import simElectricity.API.Events.TileDetachEvent;
import simElectricity.API.Events.TileRejoinEvent;
import simElectricity.Common.ConfigManager;
import simElectricity.Common.SEUtils;

public class EnergyNetEventHandler {
    public EnergyNetEventHandler() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        WorldData.onWorldUnload(event.world);
    }

    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.START)
            return;
        if (event.side != Side.SERVER)
            return;

        WorldData.getEnergyNetForWorld(event.world).onTick();
    }

    //Energy net --------------------------------------------------------------------------------------------------------------
    @SubscribeEvent
    public void onAttachEvent(TileAttachEvent event) {
        TileEntity te = event.energyTile;
        if (te.getWorld().isAirBlock(te.getPos())) {//TODO .blockExists(te.xCoord, te.yCoord, te.zCoord
            SEUtils.logInfo(te + " is added to the energy net too early!, abort!");
            return;
        }

        if (te.isInvalid()) {
            SEUtils.logInfo("Invalid tileentity " + te + " is trying to attach to the energy network, aborting");
            return;
        }

        if (!(te instanceof IBaseComponent) && !(te instanceof IComplexTile) && !(te instanceof ITransformer)) {
            SEUtils.logInfo("Unacceptable tileentity " + te + " is trying to attach to the energy network, aborting");
            return;
        }

        if (te.getWorld().isRemote) {
            SEUtils.logInfo("Client tileentity " + te + " is requesting, abort!");
            return;
        }


        WorldData.getEnergyNetForWorld(te.getWorld()).addTileEntity(te);

        if (ConfigManager.showEnergyNetInfo)
            SEUtils.logInfo("Tileentity " + te + " has attached to the energy network!");
    }

    @SubscribeEvent
    public void onTileDetach(TileDetachEvent event) {
        TileEntity te = event.energyTile;

        if (te.getWorld().isRemote) {
            SEUtils.logInfo("Client tileentity " + te + " is requesting, abort!");
            return;
        }

        WorldData.getEnergyNetForWorld(te.getWorld()).removeTileEntity(te);

        if (ConfigManager.showEnergyNetInfo)
            SEUtils.logInfo("Tileentity " + te + " has detached from the energy network!");
    }

    @SubscribeEvent
    public void onTileRejoin(TileRejoinEvent event) {
        TileEntity te = event.energyTile;

        if (te.getWorld().isRemote) {
            SEUtils.logInfo("Client tileentity " + te + " is requesting, abort!");
            return;
        }

        WorldData.getEnergyNetForWorld(te.getWorld()).rejoinTileEntity(te);

        if (ConfigManager.showEnergyNetInfo)
            SEUtils.logInfo("Tileentity " + te + " has rejoined the energy network!");
    }

    @SubscribeEvent
    public void onTileChange(TileChangeEvent event) {
        TileEntity te = event.energyTile;

        if (te.getWorld().isRemote) {
            SEUtils.logInfo("Client tileentity " + te + " is requesting, aborting");
            return;
        }

        WorldData.getEnergyNetForWorld(te.getWorld()).markForUpdate(te);

        if (ConfigManager.showEnergyNetInfo)
            SEUtils.logInfo("Tileentity " + te + " causes the energy network to update!");
    }
}
