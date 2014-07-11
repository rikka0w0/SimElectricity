package simElectricity.Client;

import cpw.mods.fml.client.config.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import simElectricity.ConfigManager;
import simElectricity.mod_SimElectricity;

public class SimEGuiConfig extends GuiConfig {
    public SimEGuiConfig(GuiScreen parentScreen) {
        super(parentScreen, new ConfigElement(ConfigManager.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), mod_SimElectricity.MODID
                , false, false, GuiConfig.getAbridgedConfigPath(ConfigManager.config.toString()));
    }
}
