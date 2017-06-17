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

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import simelectricity.api.SEAPI;

import simelectricity.common.ItemSEMgrTool;
import simelectricity.common.SEUtils;
import simelectricity.common.CommandSimE;
import simelectricity.common.ConfigManager;
import simelectricity.energynet.EnergyNetAgent;
import simelectricity.energynet.EnergyNetEventHandler;

@Mod(modid = SEUtils.MODID, name = SEUtils.NAME, version = SimElectricity.version, guiFactory = "simelectricity.client.SimEGuiFactory", dependencies = "required-after:Forge@[10.12.2.1147,)")
public class SimElectricity {
	public static final String version = "1.0.0";

    @Instance(SEUtils.MODID)
    public static SimElectricity instance;
   
    /**
     * PreInitialize
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {    	
    	//Initialize utility functions
    	SEAPI.isSELoaded = true;
    	SEAPI.energyNetAgent = new EnergyNetAgent();

        //Load configurations
        FMLCommonHandler.instance().bus().register(new ConfigManager());
        ConfigManager.init(event);

        //Register event buses
        new EnergyNetEventHandler();

        //Register creative tabs
        SEAPI.SETab = new CreativeTabs(SEUtils.MODID) {
            @Override
            @SideOnly(Side.CLIENT)
            public Item getTabIconItem() {
                return SEAPI.managementToolItem;
            }
        };
        
        
        //Register items
        SEAPI.managementToolItem = new ItemSEMgrTool();
    }
    
    @EventHandler
    public void serverStart(FMLServerStartingEvent event){
    	event.registerServerCommand(new CommandSimE());
    	SEUtils.logInfo("Server command registered", SEUtils.loader);
    }    
}
