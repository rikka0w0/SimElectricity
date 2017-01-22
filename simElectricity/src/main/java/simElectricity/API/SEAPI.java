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

package simElectricity.API;

import net.minecraft.creativetab.CreativeTabs;

import simElectricity.API.Internal.ICableRenderHelper;
import simElectricity.API.Internal.IEnergyNetAgent;
import simElectricity.API.Internal.IFluidUtil;
import simElectricity.API.Internal.INetworkManager;
import simElectricity.API.Internal.ISEUtils;

public class SEAPI {   
    public static boolean isSELoaded = false;
    
    public static IFluidUtil fluid;
    public static INetworkManager networkManager;
    public static ICableRenderHelper cableRenderHelper;
    public static ISEUtils utils;
	public static IEnergyNetAgent energyNetAgent;
    
    /**
     * Creative Tab for SimElectricity project
     */
    public static CreativeTabs SETab;

    // Block/Item

    /**
     * @param name The name of the block.
     *
     * @return The block or null if not found
     */
    //public static Block getBlock(String name) {
    //    return GameRegistry.findBlock(MODID, name);
    //}

    /**
     * @param name The name of the item.
     *
     * @return The item or null if not found
     */
    //public static Item getItem(String name) {
    //    return GameRegistry.findItem(MODID, name);
    //}
}
