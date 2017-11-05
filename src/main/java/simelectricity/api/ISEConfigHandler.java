package simelectricity.api;

import net.minecraftforge.common.config.Configuration;

public interface ISEConfigHandler {	
	/**
	 * Localization key format: <br>
	 * Category: <br>
	 * seconfig.category:CategoryName=LocalizedCategoryName<br>
	 * seconfig.category:CategoryName.tooltip=LocalizedCategoryComment<br>
	 * Property: <br>
	 * seconfig.property:PropertyName=LocalizedPropertyName<br>
	 * seconfig.property:PropertyName.tooltip=LocalizedPropertyComment<br>
	 * @param config the configuration instance
	 * @param isClient true if client rendering is avaliable
	 */
	void onConfigChanged(Configuration config, boolean isClient);
}
