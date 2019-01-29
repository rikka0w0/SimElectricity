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

import java.util.HashSet;
import java.util.Set;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import simelectricity.SimElectricity;
import simelectricity.api.ISEConfigHandler;
import simelectricity.api.internal.ISEConfigManager;
import simelectricity.energynet.EnergyNetSimulator;

public class ConfigManager implements ISEConfigManager{
	private final static Set<ISEConfigHandler> handlers = new HashSet();
	public final static String CATEGORY_ENERGYNET = "energynet";
	
    public static boolean showDebugOutput;
    public static boolean showEnergyNetInfo;
    public static String matrixSolver;
    public static int precision;
    public static int maxIteration;
    public static int shuntPN;
       

    public static void syncConfig(boolean isClient) {
    	Configuration config = SimElectricity.config;
    	ConfigManager.showDebugOutput = config.getBoolean("Enable Debug Output", CATEGORY_ENERGYNET, false, "Display debug information in the console, e.g. S->C sync notifications");
        ConfigManager.showEnergyNetInfo = config.getBoolean("Show EnergyNet Info", CATEGORY_ENERGYNET, false, "Display EnergyNet information in the console, e.g. tile attached/deteched/changed event");
        ConfigManager.matrixSolver = config.getString("Matrix Solver", CATEGORY_ENERGYNET, "QR", "The preferred matrix solving algorithm (QR is much more effective than Gaussian.). Options: QR, Gaussian. Warning: CASE SENSITIVE!");
        ConfigManager.precision = config.get(CATEGORY_ENERGYNET, "Precision", 3, "3 means that the result is accurate up to 3 decimal places").getInt();
        ConfigManager.maxIteration = config.get(CATEGORY_ENERGYNET, "Max iteration", 50, "To aviod infinite loop, the simualtor aborts the simulation when this threshold is reached").getInt();
        ConfigManager.shuntPN = config.get(CATEGORY_ENERGYNET, "RPN", 1000000000, "The resistance put in parallel with every PN junction, alleviate convergence issue").getInt();//
        
        EnergyNetSimulator.config();
        
        for (ISEConfigHandler handler: handlers) {
        	handler.onConfigChanged(isClient);
        }
    }

    //This function is supposed to be called by a client only!
    @SubscribeEvent
    public void onConfigChanged(OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(SimElectricity.MODID)) {
            ConfigManager.syncConfig(true);
        	
        	Configuration config = SimElectricity.config;
            if (config.hasChanged())
                config.save();
        }
    }
   
	@Override
	public void addConfigHandler(ISEConfigHandler handler) {
		handlers.add(handler);
	}

    @Override
    public Configuration getSEConfiguration() {
        return SimElectricity.config;
    }
}
