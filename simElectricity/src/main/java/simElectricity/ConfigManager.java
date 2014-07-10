package simElectricity;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

public class ConfigManager {
	public static Configuration config;
	
	public static boolean optimizeNodes;
	
	
	public ConfigManager(FMLPreInitializationEvent event){
		config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        
        optimizeNodes=config.get("Generals", "Optimize_Nodes", true).getBoolean();
        
        
        config.save();
	}
}
