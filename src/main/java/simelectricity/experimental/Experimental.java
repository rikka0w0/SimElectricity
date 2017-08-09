package simelectricity.experimental;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;

@Mod(modid = Experimental.modID, name = Experimental.modName, version = Experimental.version)
public class Experimental {
	public static final String modID = "sime_exp";
	public static final String modName = "SimElectricity Experimental";
	public static final String version = "1.0";
	
    @SidedProxy(clientSide="simelectricity.experimental.ClientProxy", serverSide="simelectricity.experimental.CommonProxy") 
    public static CommonProxy proxy;
}
