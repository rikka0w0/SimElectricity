package simelectricity.common;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import simelectricity.SimElectricity;
import simelectricity.energynet.EnergyNetSimulator;

@Mod.EventBusSubscriber(modid = SimElectricity.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigManager {
    public final static String CATEGORY_ENERGYNET = "energynet";

    public static BooleanValue showDebugOutput_Spec;
    public static BooleanValue showEnergyNetInfo_Spec;
    public static ForgeConfigSpec.ConfigValue<String> matrixSolver_Spec;
    public static ForgeConfigSpec.IntValue precision_Spec;
    public static ForgeConfigSpec.IntValue maxIteration_Spec;
    public static ForgeConfigSpec.IntValue shuntPN_Spec;

    public static boolean showDebugOutput;
    public static boolean showEnergyNetInfo;
    public static String matrixSolver;
    public static int precision;
    public static int maxIteration;
    public static int shuntPN;

    private static ConfigManager instace;
    private static ForgeConfigSpec configSpec;

    public static void register() {
        Pair<ConfigManager, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ConfigManager::new);
        configSpec = specPair.getRight();
        instace = specPair.getLeft();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigManager.configSpec);
    }

    public static void syncConfig() {
        showDebugOutput = showDebugOutput_Spec.get();
        showEnergyNetInfo = showEnergyNetInfo_Spec.get();
        matrixSolver = matrixSolver_Spec.get();
        precision = precision_Spec.get();
        maxIteration = maxIteration_Spec.get();
        shuntPN = shuntPN_Spec.get();

        EnergyNetSimulator.config();

//        for (ISEConfigHandler handler: handlers) {
//            handler.onConfigChanged(isClient);
//        }
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == ConfigManager.configSpec) {
            syncConfig();
            return;
        }
    }

    public static BooleanValue buildBoolean(ForgeConfigSpec.Builder builder, String modID, String key, boolean defaultVal, String comment) {
        return builder
                .comment(comment + "\r\nDefault: "+ defaultVal)
                .translation(modID + ".config." + key.toLowerCase().replace(' ', '_'))
                .define(key, defaultVal);
    }

    public static ForgeConfigSpec.IntValue buildInt(ForgeConfigSpec.Builder builder, String modID, String key, int defaultVal, String comment) {
        return buildInt(builder, modID, key, defaultVal, Integer.MIN_VALUE, Integer.MAX_VALUE, comment);
    }

    public static ForgeConfigSpec.IntValue buildInt(ForgeConfigSpec.Builder builder, String modID, String key, int defaultVal, int min, int max, String comment) {
        return builder
                .comment(comment + "\r\nDefault: "+ defaultVal)
                .translation(modID + ".config." + key.toLowerCase().replace(' ', '_'))
                .defineInRange(key, defaultVal, min, max);
    }

    public static ForgeConfigSpec.ConfigValue<String> buildString(ForgeConfigSpec.Builder builder, String modID, String key, String defaultVal, String comment) {
        return builder
                .comment(comment + "\r\nDefault: "+ defaultVal)
                .translation(modID + ".config." + key.toLowerCase().replace(' ', '_'))
                .define(key, defaultVal);
    }

    private ConfigManager(ForgeConfigSpec.Builder builder) {

        builder.push(CATEGORY_ENERGYNET);

        showDebugOutput_Spec = buildBoolean(builder, SimElectricity.MODID, "Enable Debug Output", false, "Display debug information in the console, e.g. S->C sync notifications");
        showEnergyNetInfo_Spec = buildBoolean(builder, SimElectricity.MODID,"Show EnergyNet Info", false, "Display EnergyNet information in the console, e.g. tile attached/deteched/changed event");
        matrixSolver_Spec = buildString(builder, SimElectricity.MODID,"Matrix Solver", "QR", "The preferred matrix solving algorithm (QR is much more effective than Gaussian.). Options: QR, Gaussian. Warning: CASE SENSITIVE!");
        precision_Spec = buildInt(builder, SimElectricity.MODID, "Precision", 3, "3 means that the result is accurate up to 3 decimal places");
        maxIteration_Spec = buildInt(builder, SimElectricity.MODID, "Max iteration", 50, "To aviod infinite loop, the simualtor aborts the simulation when this threshold is reached");
        shuntPN_Spec = buildInt(builder, SimElectricity.MODID, "RPN", 1000000000, "The resistance put in parallel with every PN junction, alleviate convergence issue");//

        builder.pop();
    }
}
