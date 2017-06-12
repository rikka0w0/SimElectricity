package simelectricity.essential.extensions;

import cpw.mods.fml.common.Loader;

public class ExtensionRailCraft {
	public static boolean rcLoaded;
	
	public static void postInit(){
		rcLoaded = Loader.isModLoaded("Railcraft");//FluidRegistry.getFluid("steam");
	}
}
