package simelectricity.api.internal;

import net.minecraftforge.common.config.Configuration;
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

    /**
     * Localization key format: <br>
     * Category: <br>
     * seconfig.category:CategoryName=LocalizedCategoryName<br>
     * seconfig.category:CategoryName.tooltip=LocalizedCategoryComment<br>
     * Property: <br>
     * seconfig.property:PropertyName=LocalizedPropertyName<br>
     * seconfig.property:PropertyName.tooltip=LocalizedPropertyComment<br>
     * @return the configuration instance
     */
    Configuration getSEConfiguration();
}
