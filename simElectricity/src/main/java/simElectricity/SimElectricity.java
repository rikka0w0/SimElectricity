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

package simElectricity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import simElectricity.API.ITileRenderingInfoSyncHandler;
import simElectricity.API.SEAPI;

import simElectricity.Common.SEUtils;
import simElectricity.Common.CommandSimE;
import simElectricity.Common.ConfigManager;
import simElectricity.EnergyNet.EnergyNetAgent;
import simElectricity.EnergyNet.EnergyNetEventHandler;
import simElectricity.Items.*;

@Mod(modid = SEUtils.MODID, name = SEUtils.NAME, version = SimElectricity.version, guiFactory = "simElectricity.Client.SimEGuiFactory", dependencies = "required-after:Forge@[10.12.2.1147,)")
public class SimElectricity {
	public static final String version = "1.0.0";

    @Instance(SEUtils.MODID)
    public static SimElectricity instance;

    //Instances of items
    public static ItemUltimateMultimeter ultimateMultimeter;
    public static ItemGlove itemGlove;
    public static ItemWrench itemWrench;
    
    /**
     * PreInitialize
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {    	
    	//Initialize utility functions
    	SEAPI.isSELoaded = true;
    	SEAPI.energyNetAgent = new EnergyNetAgent();
    	
    	if (event.getSide().isClient()){
    		try {
    			Class<?> clsClientRender = Class.forName("simElectricity.Client.ClientRender");
    			Method  mtdInitAPI = clsClientRender.getMethod("initClientAPI", new Class[0]);
    			mtdInitAPI.invoke(null, new Object[0]);
			} catch (Exception e) {
				SEUtils.logError("Failed to initialize client API");
			}
    	}

        //Load configurations
        FMLCommonHandler.instance().bus().register(new ConfigManager());
        ConfigManager.init(event);

        //Register event buses
        new EnergyNetEventHandler();
        new ITileRenderingInfoSyncHandler.ForgeEventHandler();

        //Register creative tabs
        SEAPI.SETab = new CreativeTabs(SEUtils.MODID) {
            @Override
            @SideOnly(Side.CLIENT)
            public Item getTabIconItem() {
                return ultimateMultimeter;
            }
        };
        
        
        //Register items
    	ultimateMultimeter = new ItemUltimateMultimeter();
    	itemGlove = new ItemGlove();
    	itemWrench = new ItemWrench();
    }

    /**
     * Initialize
     */
    @EventHandler
    public void init(FMLInitializationEvent event) {}

    /**
     * PostInitialize
     */
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {}
    
    @EventHandler
    public void serverStart(FMLServerStartingEvent event){
    	event.registerServerCommand(new CommandSimE());
    }
}
