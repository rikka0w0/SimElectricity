package simelectricity.extension.buildcraft;

import java.lang.reflect.Constructor;

import simelectricity.essential.api.ISECoverPanelFactory;
import simelectricity.essential.api.SEEAPI;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.SidedProxy;

@Mod(modid = BuildCraftExtension.modID, name = BuildCraftExtension.modName, version = BuildCraftExtension.version)
public class BuildCraftExtension {
	public static final String modID = "sime_buildcraft";
	public static final String modName = "SimElectricity BuildCraft Extension";
	public static final String version = "1.0";
	
    @SidedProxy(clientSide="simelectricity.extension.buildcraft.ClientProxy", serverSide="simelectricity.extension.buildcraft.CommonProxy") 
    public static CommonProxy proxy;
    
    @Instance(modID)
    public static BuildCraftExtension instance;
    
    public static boolean bcTransportLoaded;
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	Loader loader = Loader.instance();
		bcTransportLoaded = loader.isModLoaded("buildcrafttransport");
		
		//Attempt to load extension class
		if (bcTransportLoaded){
			try {
				Class<?> clsBCCF = Class.forName("simelectricity.extension.buildcraft.BCCoverFactory");
				Constructor<?>  constructor = clsBCCF.getConstructor();
				ISECoverPanelFactory bcCoverPanelFactory = (ISECoverPanelFactory) constructor.newInstance();
				
				SEEAPI.coverPanelRegistry.registerCoverPanelFactory(bcCoverPanelFactory);
				proxy.RegisterBlockColorHandlers();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
