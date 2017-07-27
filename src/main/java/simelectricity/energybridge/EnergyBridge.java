package simelectricity.energybridge;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = EnergyBridge.modid, name = "SimElectricity Energy Bridge", version = "1.0.0", dependencies = "required-after:sime_essential")
public class EnergyBridge {
	public static final String modid = "sime_energybridge";
	
	@SidedProxy(clientSide = "simelectricity.energybridge.ClientProxy", serverSide = "simelectricity.energybridge.CommonProxy")
	public static CommonProxy proxy;
	
	@Instance(EnergyBridge.modid)
	public static EnergyBridge instance;
	
    /**
     * PreInitialize
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {    
    	//Register Blocks
    	
    }
	
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	//Register TileEntities
    	//GameRegistry.registerTileEntity
    	
        //Register GUI handler
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);   
    }
}
