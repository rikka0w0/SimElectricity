package simelectricity.essential;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.cable.CableWatchEventHandler;
import simelectricity.essential.cable.CoverPanelRegistry;
import simelectricity.essential.cable.SECoverPanelFactory;
import simelectricity.essential.utils.network.MessageContainerSync;


@Mod(modid = Essential.modID, name = "SimElectricity Essential", dependencies = "required-after:simelectricity")
public class Essential {
	public final static String modID = "sime_essential";
	
    @SidedProxy(clientSide="simelectricity.essential.ClientProxy", serverSide="simelectricity.essential.CommonProxy") 
    public static CommonProxy proxy;
	
    @Instance(modID)
    public static Essential instance;
    
    public SimpleNetworkWrapper networkChannel;
	
    /**
     * PreInitialize
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {    	
    	ItemRegistry.registerItems();
    	BlockRegistry.registerBlocks();
    	
    	SEEAPI.coverPanelRegistry = new CoverPanelRegistry();
        
        networkChannel = NetworkRegistry.INSTANCE.newSimpleChannel(modID);
        networkChannel.registerMessage(MessageContainerSync.Handler.class, MessageContainerSync.class, 0, Side.CLIENT);
        networkChannel.registerMessage(MessageContainerSync.Handler.class, MessageContainerSync.class, 1, Side.SERVER);
        
        proxy.preInit();
    }
    
    /**
     * Initialize
     */
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	BlockRegistry.registerTileEntities();
    	
    	proxy.init();
    	
    	MinecraftForge.EVENT_BUS.register(new CableWatchEventHandler());
    	
        //Register GUI handler
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
    }

    /**
     * PostInitialize
     */
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	SEEAPI.coverPanelRegistry.registerCoverPanelFactory(new SECoverPanelFactory());
    }
}
