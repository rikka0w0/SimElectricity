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

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import simElectricity.API.Util;
import simElectricity.Common.CommonProxy;
import simElectricity.Common.ConfigManager;
import simElectricity.Common.Core.SEBlocks;
import simElectricity.Common.Core.SEItems;
import simElectricity.Common.EnergyNet.EnergyNetEventHandler;
import simElectricity.Common.Network.MessageTileEntityUpdate;
import simElectricity.Common.Network.NetworkManager;

@Mod(modid = Util.MODID, name = Util.NAME, version = "1.0.0", guiFactory = "simElectricity.Client.SimEGuiFactory", dependencies = "required-after:Forge@[10.12.2.1147,)")
public class SimElectricity {

    /**
     * Server and Client Proxy
     */
    @SidedProxy(clientSide = "simElectricity.Client.ClientProxy", serverSide = "simElectricity.Common.CommonProxy")
    public static CommonProxy proxy;

    @Instance(Util.MODID)
    public static SimElectricity instance;

    public SimpleNetworkWrapper networkChannel;

    /**
     * PreInitialize
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        //Load Configs
        FMLCommonHandler.instance().bus().register(new ConfigManager());
        ConfigManager.init(event);

        //Add to event bus
        new NetworkManager();
        new EnergyNetEventHandler();

        //CreativeTab
        Util.SETab = new CreativeTabs(Util.MODID) {
            @Override
            @SideOnly(Side.CLIENT)
            public Item getTabIconItem() {
                return Item.getItemFromBlock(SEBlocks.quantumGenerator);
            }
        };

        //Register Blocks
        SEBlocks.preInit();

        //Register Items
        SEItems.init();

        //Register network channel
        networkChannel = NetworkRegistry.INSTANCE.newSimpleChannel(Util.MODID);
        networkChannel.registerMessage(MessageTileEntityUpdate.Handler.class, MessageTileEntityUpdate.class, 0, Side.CLIENT);
        networkChannel.registerMessage(MessageTileEntityUpdate.Handler.class, MessageTileEntityUpdate.class, 1, Side.SERVER);
    }

    /**
     * Initialize
     */
    @EventHandler
    public void init(FMLInitializationEvent event) {
        //Register GUI handler
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

        //Initialize network proxy
        proxy.registerTileEntitySpecialRenderer();

        //Register TileEntities
        SEBlocks.init();
    }

    /**
     * PostInitialize
     */
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }
}
