package simelectricity.essential.client;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import rikka.librikka.ForgeConfigHelper;
import simelectricity.essential.Essential;

public class ClientConfigs {
	public final static String CATEGORY_RENDERING = "rendering";

	public static ForgeConfigSpec.IntValue parabolaRenderSteps;

	private static ForgeConfigSpec configSpec;

	public static void register() {
		Pair<ClientConfigs, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfigs::new);
		configSpec = specPair.getRight();

		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfigs.configSpec);
	}

	private ClientConfigs(ForgeConfigSpec.Builder builder) {
		builder.push(CATEGORY_RENDERING);
		parabolaRenderSteps = ForgeConfigHelper.intVal(builder, Essential.MODID, "Cable Render Step Size", 
				12, 0, Integer.MAX_VALUE,
				"The higher this number is, the smoother the catenary cable will be. (must be EVEN! CLIENT ONLY!)");
		builder.pop();
	}
}
