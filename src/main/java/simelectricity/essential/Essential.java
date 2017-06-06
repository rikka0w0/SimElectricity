package simelectricity.essential;

import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.cable.CoverPanelFactory;
import simelectricity.essential.extensions.ExtensionBuildCraft;
import simelectricity.essential.utils.ITileRenderingInfoSyncHandler;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Essential.modID, name = "SimElectricity Essential", dependencies = "required-after:simelectricity")
public class Essential {
	public final static String modID = "sime_essential";
	
    @SidedProxy(clientSide="simelectricity.essential.ClientProxy", serverSide="simelectricity.essential.CommonProxy") 
    public static CommonProxy proxy;
	
    @Instance(modID)
    public static Essential instance;
	
    /**
     * PreInitialize
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {    	
    	ItemRegistery.registerItems();
    	BlockRegistery.registerBlocks();
    	
    	SEEAPI.coverPanelFactory = new CoverPanelFactory();
    	
        //Register Forge Event Handlers
        new ITileRenderingInfoSyncHandler.ForgeEventHandler();
    }
    
    /**
     * Initialize
     */
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	BlockRegistery.registerTileEntities();
    	
    	proxy.registerRenders();
    	
    }

    /**
     * PostInitialize
     */
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	ExtensionBuildCraft.postInit();
    }
}
