package simelectricity.api.internal;

import simelectricity.api.ISEConfigHandler;

/**
 *  API users should NOT implement this interface!.
 */
public interface ISEConfigManager {
    /**
     * Allow child-Mods to access the configuration file and add custom configuration categories and properties<br>
     * During FMLPreInitializationEvent, call this method to add custom ISEConfigHandler
     */
	void addConfigHandler(ISEConfigHandler handler);
}
