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
import simElectricity.Common.GlobalEventHandler;
import simElectricity.Common.Network.PacketPipeline;
import simElectricity.Common.Network.PacketTileEntityFieldUpdate;
import simElectricity.Common.Network.PacketTileEntitySideUpdate;

@Mod(modid = Util.MODID, name = Util.NAME, version = "1.0.0", guiFactory = "simElectricity.Client.SimEGuiFactory", dependencies = "required-after:Forge@[10.12.2.1147,)")
public class SimElectricity {

    /**
     * Server and Client Proxy
     */
    @SidedProxy(clientSide = "simElectricity.Client.ClientProxy", serverSide = "simElectricity.Common.CommonProxy")
    public static CommonProxy proxy;

    @Instance(Util.MODID)
    public static SimElectricity instance;

    public PacketPipeline packetPipeline = new PacketPipeline();

    /**
     * PreInitialize
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        //Load Configs
        FMLCommonHandler.instance().bus().register(new ConfigManager());
        ConfigManager.init(event);

        //Add to event bus
        new GlobalEventHandler();
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
    }

    /**
     * Initialize
     */
    @EventHandler
    public void init(FMLInitializationEvent event) {
        //Register GUI handler
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

        //Initialize network proxy
        packetPipeline.initialize();
        proxy.registerTileEntitySpecialRenderer();

        //Register TileEntities
        SEBlocks.init();
    }

    /**
     * PostInitialize
     */
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        //Register network packets
        packetPipeline.registerPacket(PacketTileEntityFieldUpdate.class);
        packetPipeline.registerPacket(PacketTileEntitySideUpdate.class);
        packetPipeline.postInitialize();
    }
}
