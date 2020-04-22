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

package simelectricity;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import simelectricity.api.SEAPI;
import simelectricity.common.CommandSimE;
import simelectricity.common.ConfigManager;
import simelectricity.common.ItemSEMgrTool;
import simelectricity.common.SELogger;
import simelectricity.energynet.EnergyNetAgent;

@Mod(SimElectricity.MODID)
public class SimElectricity {
    public static final String MODID = "simelectricity";
//    public static final String NAME = "SimElectricity";
//    public static final String version = "1.0.0";

    public static SimElectricity instance = null;

    public SimElectricity() {
        if (instance == null)
            instance = this;
        else
            throw new RuntimeException("Duplicated Class Instantiation: SimElectricity");
        
        ConfigManager.register();

        //Initialize SEAPI
        SEAPI.isSELoaded = true;
        SEAPI.energyNetAgent = new EnergyNetAgent();
    }
    
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public final static class ModEventBusHandler {		// FMLJavaModLoadingContext.get().getModEventBus()
    	@SubscribeEvent
    	public static void newRegistry(RegistryEvent.NewRegistry event) {
            // Register creative tabs
            SEAPI.SETab = new ItemGroup(SimElectricity.MODID) {
                @Override
                @OnlyIn(Dist.CLIENT)
                public ItemStack createIcon() {
                    return new ItemStack(SEAPI.managementToolItem);
                }
            };
    	}

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            //Register items
            SEAPI.managementToolItem = new ItemSEMgrTool();
    	    event.getRegistry().register(SEAPI.managementToolItem);
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public final static class ForgeEventBusHandler{	// MinecraftForge.EVENT_BUS MinecraftForgeEventsHandler
        @SubscribeEvent
        public static void onServerStarting(FMLServerStartingEvent e) {
        	CommandSimE.register(e.getCommandDispatcher());
            SELogger.logInfo(SELogger.loader, "Server command registered");
        }
    }

}
