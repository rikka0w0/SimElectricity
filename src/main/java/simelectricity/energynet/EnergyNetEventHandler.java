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

import net.minecraft.world.level.Level;
//import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.LogicalSide;
//import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import simelectricity.SimElectricity;

@Mod.EventBusSubscriber(modid = SimElectricity.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnergyNetEventHandler {
    @SubscribeEvent
    public static void onWorldUnload(Unload event) {
    	// TODO: Check Type: World
        EnergyNetAgent.onWorldUnload((Level)event.getWorld());
    }

    //Pre -> Entities -> TileEntitis -> Post
    @SubscribeEvent
    public static void tick(TickEvent.WorldTickEvent event) {
        if (event.side != LogicalSide.SERVER)
            return;
        if (event.phase != TickEvent.Phase.START)
            return;

        EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(event.world);
        energyNet.onPreTick();
    }
}
