package simelectricity.essential;


import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.ISEConfigHandler;
import simelectricity.api.SEAPI;

public class ConfigProvider implements ISEConfigHandler {
	private final Configuration config;

	public ConfigProvider(boolean isClient) {
		this.config = SEAPI.configManager.getSEConfiguration();
		SEAPI.configManager.addConfigHandler(this);

		// Load the config for the first time
		onConfigChanged(isClient);
	};
	
	@SideOnly(Side.CLIENT)
    public static int parabolaRenderSteps;
	
	public static final String CATEGORY_CONVERSION = "conversion";
	public static float joule2rf; 
	
	@Override
	public void onConfigChanged(boolean isClient) {
        //Client-only configurations
        if (isClient) {
        	this.parabolaRenderSteps = config.getInt("Cable Render Step Size", Configuration.CATEGORY_CLIENT, 12, 0, Integer.MAX_VALUE, "The higher this number is, the smoother the catenary cable will be. (must be EVEN! CLIENT ONLY!)");
        }
        
        this.joule2rf = config.getFloat("Joule to RF conversion ratio", CATEGORY_CONVERSION, 1, 0, Float.MAX_VALUE, "This number determines how many RF equal to 1 Joule");
	}
}
