/*
 * Copyright (C) 2014 SimElectricity
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package simElectricity.Common;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;

public class ConfigManager {

    public static Configuration config;

    /**
     * Enable Optimized Nodes
     */
    public static boolean optimizeNodes;
    public static boolean showEnergyNetInfo;
    public static int parabolaRenderSteps;


    public static void init(FMLPreInitializationEvent event) {
        if (config == null) {
            config = new Configuration(event.getSuggestedConfigurationFile());
            syncConfig();
        }
    }

    private static void syncConfig() {
        optimizeNodes = config.get(Configuration.CATEGORY_GENERAL, "OptimizeNodes", false, "A function that can improve the performance by reducing the total number of conductor nodes (May be buggy)").getBoolean();
        showEnergyNetInfo = config.get(Configuration.CATEGORY_GENERAL, "ShowEnergyNetInfo", false, "Display energy net infomations, such as joining/leaving/changging").getBoolean();
        parabolaRenderSteps = config.get(Configuration.CATEGORY_GENERAL, "ParabolaRenderSteps", 12, "Decides how smooth the parabola cable is(must be a even number!Client ONLY!)").getInt(12);
        
        
        if (config.hasChanged())
            config.save();
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equalsIgnoreCase(SEUtils.MODID))
            syncConfig();
    }
}
