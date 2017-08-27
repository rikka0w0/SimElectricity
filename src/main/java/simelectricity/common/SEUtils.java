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

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;
import simelectricity.SimElectricity;

/**
 * Created by <Meow J> on 8/6/2014.
 *
 * @author Meow J
 */
public enum SEUtils {
    //Info Sources
    loader("ModLoader"),
    general("General"),
    simulator("Simulator"),
    energyNet("EnergyNet");


    private final String text;

    SEUtils(String text) {
        this.text = text;
    }

    public static void logInfo(Object object, SEUtils source) {
        if (!ConfigManager.showEnergyNetInfo && (source == SEUtils.energyNet || source == SEUtils.simulator))
            return;

        FMLLog.log(SimElectricity.NAME, Level.INFO, source + "|" + String.valueOf(object));
    }

    public static void logWarn(Object object, SEUtils source) {
        FMLLog.log(SimElectricity.NAME, Level.WARN, source + "|" + String.valueOf(object));
    }

    public static void logError(Object object, SEUtils source) {
        FMLLog.log(SimElectricity.NAME, Level.ERROR, source + "|" + String.valueOf(object));
    }

    public static void logFatal(Object object, SEUtils source) {
        FMLLog.log(SimElectricity.NAME, Level.FATAL, source + "|" + String.valueOf(object));
    }

    public static TileEntity getTileEntityOnDirection(TileEntity te, EnumFacing direction) {
        return te.getWorld().getTileEntity(te.getPos().offset(direction));
    }

    @Override
    public String toString() {
        return this.text;
    }
}