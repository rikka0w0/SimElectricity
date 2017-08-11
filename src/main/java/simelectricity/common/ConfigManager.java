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

package simelectricity.common;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.SimElectricity;

public class ConfigManager {
    public static Configuration config;

    /**
     * Enable Optimized Nodes
     */
    public static boolean showEnergyNetInfo;   
    public static String matrixSolver;
    public static int precision;
    public static int maxIteration;
    public static int shuntPN;

    @SideOnly(Side.CLIENT)
    public static int parabolaRenderSteps;
    
    public static void init(FMLPreInitializationEvent event) {
        if (config == null) {
            config = new Configuration(event.getSuggestedConfigurationFile());
            syncConfig(event.getSide().isClient());
        }
    }

    private static void syncConfig(boolean isClient) {
        showEnergyNetInfo = config.get(Configuration.CATEGORY_GENERAL, "Show Energy Net Info", false, "Display energy net information, such as tile attached/deteched/changed").getBoolean();
        matrixSolver = config.getString("Matrix Solver", Configuration.CATEGORY_GENERAL, "QR", "The algorithms used to perform matrix calculation(QR is much more effective than Gaussian.).Options: QR, Gaussian", new String[] { "QR", "Gaussian" });
        precision = config.get(Configuration.CATEGORY_GENERAL, "Precision", 3, "3 means the result is precise up to at least 3 decimal places").getInt();
        maxIteration = config.get(Configuration.CATEGORY_GENERAL, "Max iteration", 50, "The maximum number of iteration before abort the simulation").getInt();
        shuntPN = config.get(Configuration.CATEGORY_GENERAL, "RPN", 1000000000, "The resistance connected beside every PN junction").getInt();        
        
        //Client-only configurations
        if (isClient){
            parabolaRenderSteps = config.get(Configuration.CATEGORY_GENERAL, "Parabola Render Steps", 12, "Determines how smooth the parabola cable is(must be a even number!CLIENT ONLY!)").getInt();
        }
        
        if (config.hasChanged())
            config.save();
        
    }

    //This function is supposed to be called by a client only!
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(SimElectricity.MODID))
            syncConfig(true);
    }
}
