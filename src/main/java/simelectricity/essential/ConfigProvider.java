package simelectricity.essential;


import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.ISEConfigHandler;
import simelectricity.api.SEAPI;

public class ConfigProvider implements ISEConfigHandler {
	public ConfigProvider() {
		SEAPI.configManager.addConfigHandler(this);
	};
	
	@SideOnly(Side.CLIENT)
    public static int parabolaRenderSteps;
	
	@Override
	public void onConfigChanged(Configuration config, boolean isClient) {
        //Client-only configurations
        if (isClient) {
        	ConfigProvider.parabolaRenderSteps = config.get(Configuration.CATEGORY_CLIENT, "Cable Render Step Size", 12, "The higher this number is, the smoother the catenary cable will be. (must be EVEN! CLIENT ONLY!)").getInt();//
        }
	}
}
