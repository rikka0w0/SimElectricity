package simelectricity.essential.extensions;

import java.lang.reflect.Constructor;

import cpw.mods.fml.common.Loader;

import simelectricity.essential.api.internal.ISECoverPanelFactory;

public class ExtensionBuildCraft {
	public static boolean bcTransportLoaded;
	public static ISECoverPanelFactory bcCoverPanelFactory;
	
	public static void postInit(){
		bcTransportLoaded = Loader.isModLoaded("BuildCraft|Transport");
		
		//Attempt to load extension class
		if (bcTransportLoaded){
			try {
				Class<?> clsBCCF = Class.forName("simelectricity.essential.extensions.BCCoverFactory");
				Constructor<?>  constructor = clsBCCF.getConstructor();
				bcCoverPanelFactory = (ISECoverPanelFactory) constructor.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
