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

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.SEAPI;
import simelectricity.common.CommandSimE;
import simelectricity.common.ConfigManager;
import simelectricity.common.ItemSEMgrTool;
import simelectricity.common.SELogger;
import simelectricity.energynet.EnergyNetAgent;
import simelectricity.energynet.EnergyNetEventHandler;

@Mod(modid = SimElectricity.MODID, name = SimElectricity.NAME, version = SimElectricity.version, guiFactory = "simelectricity.client.SimEGuiFactory")
public class SimElectricity {
    public static final String MODID = "simelectricity";
    public static final String NAME = "SimElectricity";
    public static final String version = "1.0.0";
    
    @Mod.Instance(MODID)
    public static SimElectricity instance;
    
    public static Configuration config;
    
    /**
     * PreInitialize
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        //Initialize SEAPI
        SEAPI.isSELoaded = true;
        SEAPI.energyNetAgent = new EnergyNetAgent();
        SEAPI.configManager = new ConfigManager();
        
        //Load configurations
        FMLCommonHandler.instance().bus().register(new ConfigManager());
        this.config = new Configuration(event.getSuggestedConfigurationFile());

        //Register event buses
        new EnergyNetEventHandler();

        ConfigManager.syncConfig(event.getSide().isClient());
    }
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }
    
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	if (config.hasChanged())
    		config.save();
    }
    
    @Mod.EventBusSubscriber(modid = MODID)
    public static class RegistrationHandler {
    	@SubscribeEvent
    	public static void newRegistry(RegistryEvent.NewRegistry event) {
            //Register creative tabs
            SEAPI.SETab = new CreativeTabs(SimElectricity.MODID) {
                @Override
                @SideOnly(Side.CLIENT)
                public ItemStack getTabIconItem() {
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
    
    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandSimE());
        SELogger.logInfo(SELogger.loader, "Server command registered");
    }
}
