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

import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;
import simelectricity.SimElectricity;

/**
 * Created by <Meow J> on 8/6/2014.
 *
 * @author Meow J
 */
public enum SELogger {
    //Info Sources
    loader("ModLoader", false),
    general("General", false),
    simulator("Simulator", false),
    energyNet("EnergyNet", false),
    sync("Sync", true),
    client("Client", true);


    private final String text;
    public final boolean isDebugInfo;

    SELogger(String text, boolean isDebugInfo) {
        this.text = text;
        this.isDebugInfo = isDebugInfo;
    }

    public static void logInfo(SELogger source, Object object) {
        if (!ConfigManager.showDebugOutput && source.isDebugInfo)
        	return;
        
        if (!ConfigManager.showEnergyNetInfo && (source == SELogger.energyNet || source == SELogger.simulator))
            return;
        
        FMLLog.log(SimElectricity.NAME, Level.INFO, source + "|" + object);
    }

    public static void logWarn(SELogger source, Object object) {
        FMLLog.log(SimElectricity.NAME, Level.WARN, source + "|" + object);
    }

    public static void logError(SELogger source, Object object) {
        FMLLog.log(SimElectricity.NAME, Level.ERROR, source + "|" + object);
    }

    public static void logFatal(SELogger source, Object object) {
        FMLLog.log(SimElectricity.NAME, Level.FATAL, source + "|" + object);
    }

    @Override
    public String toString() {
        return this.text;
    }
}