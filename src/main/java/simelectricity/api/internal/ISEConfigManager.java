package simelectricity.api.internal;

import simelectricity.api.ISEConfigHandler;

public interface ISEConfigManager {
    /**
     * Allow child-Mods to access the configuration file and add custom configuration categories and properties<br>
     * Add custom ISEConfigHandler in your FMLPreInitializationEvent handler
     */
	void addConfigHandler(ISEConfigHandler handler);
}
