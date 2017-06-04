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

import cpw.mods.fml.common.FMLLog;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;

/**
 * Created by <Meow J> on 8/6/2014.
 *
 * @author Meow J
 */
public enum SEUtils{
	//Info Sources
	loader("ModLoader"),
	general("General"),
	simulator("Simulator"),
	energyNet("EnergyNet");
	
    public static final String MODID = "SimElectricity";
    public static final String NAME = "SimElectricity";

    private String text;
    
	SEUtils(String text) {
		this.text = text;
	}
	
	@Override
	public String toString(){
		return text;
	}

    public static void logInfo(Object object, SEUtils source) {
    	if (!ConfigManager.showEnergyNetInfo && (source == energyNet || source == simulator))
    		return;
    	
        FMLLog.log(NAME, Level.INFO, source + "|" + String.valueOf(object));
    }

    public static void logWarn(Object object, SEUtils source) {
        FMLLog.log(NAME, Level.WARN, source + "|" + String.valueOf(object));
    }

    public static void logError(Object object, SEUtils source) {
        FMLLog.log(NAME, Level.ERROR, source + "|" + String.valueOf(object));
    }

    public static void logFatal(Object object, SEUtils source) {
        FMLLog.log(NAME, Level.FATAL, source + "|" + String.valueOf(object));
    }
    
    
	public static TileEntity getTileEntityOnDirection(TileEntity te, ForgeDirection direction){
    	return te.getWorldObj().getTileEntity(
                te.xCoord + direction.offsetX,
                te.yCoord + direction.offsetY,
                te.zCoord + direction.offsetZ);
	}
}