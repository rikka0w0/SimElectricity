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

package simElectricity.Common.EnergyNet;

import net.minecraft.world.World;

import java.util.Map;
import java.util.WeakHashMap;

public class WorldData {
    @SuppressWarnings("unchecked")
    public static Map<World, WorldData> mapping = new WeakHashMap();

    public EnergyNet energyNet;

    /**
     * Return the instance of energyNet for a specific world
     * <p/>
     * If target not exist, it will automatically be created
     */
    public static EnergyNet getEnergyNetForWorld(World world) {
        if (world == null)
            throw new IllegalArgumentException("world is null");

        WorldData ret = mapping.get(world);

        if (ret == null) {
            ret = new WorldData();
            ret.energyNet = new EnergyNet(world);
            mapping.put(world, ret);
        }

        return ret.energyNet;
    }

    public static void onWorldUnload(World world) {
        mapping.remove(world);
    }
}