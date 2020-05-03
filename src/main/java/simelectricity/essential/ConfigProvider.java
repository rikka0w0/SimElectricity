package simelectricity.essential;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = Essential.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigProvider {
	public final static String CATEGORY_ENERGYNET = "energynet";
	public final static String CATEGORY_RENDERING = "rendering";

    public static ForgeConfigSpec.IntValue parabolaRenderSteps_Spec;
    public static ForgeConfigSpec.DoubleValue joule2rf_Spec;
    
    public static int parabolaRenderSteps;
	
	public static double joule2rf; 
    
    private static ConfigProvider instace;
    private static ForgeConfigSpec configSpec;
    
    public static void register() {
        Pair<ConfigProvider, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ConfigProvider::new);
        configSpec = specPair.getRight();
        instace = specPair.getLeft();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigProvider.configSpec);
    }

    public static void syncConfig() {
    	joule2rf = joule2rf_Spec.get();
    	parabolaRenderSteps = parabolaRenderSteps_Spec.get();
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == ConfigProvider.configSpec) {
            syncConfig();
            return;
        }
    }

    public static ForgeConfigSpec.IntValue buildInt(ForgeConfigSpec.Builder builder, String modID, String key, int defaultVal, int min, int max, String comment) {
        return builder
                .comment(comment + "\r\nDefault: "+ defaultVal)
                .translation(modID + ".config." + key.toLowerCase().replace(' ', '_'))
                .defineInRange(key, defaultVal, min, max);
    }
    
    public static ForgeConfigSpec.DoubleValue buildDouble(ForgeConfigSpec.Builder builder, String modID, String key, double defaultVal, double min, double max, String comment) {
        return builder
                .comment(comment + "\r\nDefault: "+ defaultVal)
                .translation(modID + ".config." + key.toLowerCase().replace(' ', '_'))
                .defineInRange(key, defaultVal, min, max);
    }

    private ConfigProvider(ForgeConfigSpec.Builder builder) {
    	builder.push(CATEGORY_ENERGYNET);
        joule2rf_Spec = buildDouble(builder, Essential.MODID, "Joule to RF conversion ratio", 1, 0, Double.MAX_VALUE, "This number determines how many RF equal to 1 Joule");
    	builder.pop();
    	
    	if (EffectiveSide.get().isClient()) {
	        builder.push(CATEGORY_RENDERING);
	        parabolaRenderSteps_Spec = buildInt(builder, Essential.MODID, "Cable Render Step Size", 12, 0, Integer.MAX_VALUE, "The higher this number is, the smoother the catenary cable will be. (must be EVEN! CLIENT ONLY!)");
	        builder.pop();
    	}
    }      
}
