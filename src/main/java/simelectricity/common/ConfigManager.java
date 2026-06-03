package simelectricity.common;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import rikka.librikka.ForgeConfigHelper;

import org.apache.commons.lang3.tuple.Pair;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import simelectricity.SimElectricity;
import simelectricity.energynet.EnergyNet;
import simelectricity.energynet.EnergyNetAgent;
import simelectricity.essential.Essential;

@EventBusSubscriber(modid = SimElectricity.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ConfigManager {
    public final static String CATEGORY_ENERGYNET = "energynet";

    public static BooleanValue showDebugOutput_Spec;
    public static BooleanValue showEnergyNetInfo_Spec;
    public static ModConfigSpec.IntValue maxIteration_Spec;
    public static ModConfigSpec.ConfigValue<String> matrixSolver;
    public static ModConfigSpec.IntValue precision;
    public static ModConfigSpec.IntValue shuntPN;
    public static ModConfigSpec.DoubleValue joule2rf;

    public static boolean showDebugOutput;
    public static boolean showEnergyNetInfo;
    public static int maxIteration;

    private static ModConfigSpec configSpec;

    public static void register() {
        Pair<ConfigManager, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ConfigManager::new);
        configSpec = specPair.getRight();

        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, ConfigManager.configSpec);
    }

    public static void syncConfig() {
        showDebugOutput = showDebugOutput_Spec.get();
        showEnergyNetInfo = showEnergyNetInfo_Spec.get();
        maxIteration = maxIteration_Spec.get();

        EnergyNetAgent.mapping.values().forEach(EnergyNet::notifyConfigChanged);
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == ConfigManager.configSpec) {
            syncConfig();
            return;
        }
    }

    private ConfigManager(ModConfigSpec.Builder builder) {
        builder.push(CATEGORY_ENERGYNET);

        showDebugOutput_Spec = ForgeConfigHelper.boolVal(builder, SimElectricity.MODID, "Enable Debug Output", false, "Display debug information in the console, e.g. S->C sync notifications");
        showEnergyNetInfo_Spec = ForgeConfigHelper.boolVal(builder, SimElectricity.MODID,"Show EnergyNet Info", false, "Display EnergyNet information in the console, e.g. tile attached/deteched/changed event");
        matrixSolver = ForgeConfigHelper.stringVal(builder, SimElectricity.MODID,"Matrix Solver", "QR", "The preferred matrix solving algorithm (QR is much more effective than Gaussian.). Options: QR, Gaussian. Warning: CASE SENSITIVE!");
        precision = ForgeConfigHelper.intVal(builder, SimElectricity.MODID, "Precision", 3, "3 means that the result is accurate up to 3 decimal places");
        maxIteration_Spec = ForgeConfigHelper.intVal(builder, SimElectricity.MODID, "Max iteration", 50, "To aviod infinite loop, the simualtor aborts the simulation when this threshold is reached");
        shuntPN = ForgeConfigHelper.intVal(builder, SimElectricity.MODID, "RPN", 1000000000, "The resistance put in parallel with every PN junction, alleviate convergence issue");//
        joule2rf = ForgeConfigHelper.doubleVal(builder, Essential.MODID, "Joule to RF conversion ratio", 1, 0, Double.MAX_VALUE, "This number determines how many RF equal to 1 Joule");

        builder.pop();
    }
}
