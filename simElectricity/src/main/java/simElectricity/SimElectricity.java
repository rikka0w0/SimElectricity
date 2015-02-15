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


import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simElectricity.API.Util;
import simElectricity.Common.CommandSimE;
import simElectricity.Common.CommonProxy;
import simElectricity.Common.ConfigManager;
import simElectricity.Common.Core.SEBlocks;
import simElectricity.Common.Core.SEItems;
import simElectricity.Common.EnergyNet.EnergyNetEventHandler;
import simElectricity.Common.Network.MessageTileEntityUpdate;
import simElectricity.Common.Network.NetworkManager;

@Mod(modid = Util.MODID, name = Util.NAME, version = SimElectricity.version, guiFactory = "simElectricity.Client.SimEGuiFactory", dependencies = "required-after:Forge@[10.12.2.1147,)")
public class SimElectricity {
    public static final String version = "1.0.0";

    /**
     * Server and Client Proxy
     */
    @SidedProxy(clientSide = "simElectricity.Client.ClientProxy", serverSide = "simElectricity.Common.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(Util.MODID)
    public static SimElectricity instance;

    public SimpleNetworkWrapper networkChannel;

    /**
     * PreInitialize
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Util.isSELoaded = true;

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
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        //Register GUI handler
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

        //Initialize network proxy
        proxy.registerRenders();

        //Register TileEntities
        SEBlocks.init();
    }

    /**
     * PostInitialize
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandSimE());
    }
}
