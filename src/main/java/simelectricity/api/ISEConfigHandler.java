package simelectricity.api;

import net.minecraftforge.common.config.Configuration;

public interface ISEConfigHandler {	
	/**
	 * @param isClient true if client rendering is available
	 */
	void onConfigChanged(boolean isClient);
}
