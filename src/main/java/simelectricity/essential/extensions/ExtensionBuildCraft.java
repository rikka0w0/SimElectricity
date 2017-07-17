package simelectricity.essential.extensions;

import java.lang.reflect.Constructor;

import cpw.mods.fml.common.Loader;

import simelectricity.essential.api.ISECoverPanelFactory;
import simelectricity.essential.api.SEEAPI;

public class ExtensionBuildCraft {
	public static boolean bcTransportLoaded;
	
	public static void postInit(){
		bcTransportLoaded = Loader.isModLoaded("BuildCraft|Transport");
		
		//Attempt to load extension class
		if (bcTransportLoaded){
			try {
				Class<?> clsBCCF = Class.forName("simelectricity.essential.extensions.buildcraft.BCCoverFactory");
				Constructor<?>  constructor = clsBCCF.getConstructor();
				ISECoverPanelFactory bcCoverPanelFactory = (ISECoverPanelFactory) constructor.newInstance();
				
				SEEAPI.coverPanelRegistry.registerCoverPanelFactory(bcCoverPanelFactory);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
