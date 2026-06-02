package simelectricity.essential.client;

import org.apache.commons.lang3.tuple.Pair;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import rikka.librikka.ForgeConfigHelper;
import simelectricity.essential.Essential;

public class ClientConfigs {
	public final static String CATEGORY_RENDERING = "rendering";

	public static ModConfigSpec.IntValue parabolaRenderSteps;

	private static ModConfigSpec configSpec;

	public static void register() {
		Pair<ClientConfigs, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ClientConfigs::new);
		configSpec = specPair.getRight();

		ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.CLIENT, ClientConfigs.configSpec);
	}

	private ClientConfigs(ModConfigSpec.Builder builder) {
		builder.push(CATEGORY_RENDERING);
		parabolaRenderSteps = ForgeConfigHelper.intVal(builder, Essential.MODID, "Cable Render Step Size", 
				12, 0, Integer.MAX_VALUE,
				"The higher this number is, the smoother the catenary cable will be. (must be EVEN! CLIENT ONLY!)");
		builder.pop();
	}
}
