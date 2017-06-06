package simelectricity.Templates;

import simelectricity.api.SEAPI;
import simelectricity.Templates.Utils.MessageGui;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;




@Mod(modid = SETemplate.MODID, name = SETemplate.NAME, version = SETemplate.VER, dependencies = "required-after:Forge@[10.12.2.1147,); required-after:simelectricity")
public class SETemplate {
	public static final String MODID = "SETemplate";
	public static final String NAME = "SETemplate";
	public static final String VER = "0.99";
	
    @Instance(SETemplate.MODID)
    public static SETemplate instance;
    
    public SimpleNetworkWrapper networkChannel;
    
    /**
     * Server and Client Proxy
     */
    @SidedProxy(clientSide = "simelectricity.Templates.ClientProxy", serverSide = "simelectricity.Templates.CommonProxy")
    public static CommonProxy proxy;
    
    
    /**
     * PreInitialize
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	if (SEAPI.SETab == null)
    		throw new RuntimeException("SETab is null!");
    	
        //Register Blocks
        SEBlocks.preInit();

        //Register Items
        SEItems.init();
        
        networkChannel = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        networkChannel.registerMessage(MessageGui.Handler.class, MessageGui.class, 0, Side.CLIENT);
        networkChannel.registerMessage(MessageGui.Handler.class, MessageGui.class, 1, Side.SERVER);
    }
    
    
    /**
     * Initialize
     */
    @EventHandler
    public void init(FMLInitializationEvent event) {
        //Register GUI handler
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);


        proxy.registerTileEntitySpecialRenderer();

        //Register TileEntities
        SEBlocks.init();
    }
}
