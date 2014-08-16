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

import simElectricity.API.EnergyTile.IBaseComponent;
import simElectricity.API.EnergyTile.IConductor;

import java.util.*;

public class VirtualConductor implements IConductor {

    private static Map<IConductor, VirtualConductor> map = new HashMap<IConductor, VirtualConductor>();

    public static VirtualConductor getVirtualConductor(IConductor conductor) {
        return map.get(conductor);
    }

    public static boolean conductorInVirtual(IConductor conductor) {
        return map.containsKey(conductor);
    }

    public static Set<IConductor> allConductorInVirtual() {
        return map.keySet();
    }

    public static void mapClear() {
        map.clear();
    }

    private List<IConductor> contains = new ArrayList<IConductor>();
    private IBaseComponent[] connections = { null, null };
    private float totalResistance = 0;

    @Override
    public double getResistance() {
        return totalResistance;
    }

    @Override
    public int getColor() {
        return 0;
    }

    public boolean append(IConductor conductor) {
        boolean result = this.contains.add(conductor);
        if (result) {
            totalResistance += conductor.getResistance();
            VirtualConductor.map.put(conductor, this);

            if (connections[0] == conductor)
                connections[0] = null;
            else if (connections[1] == conductor)
                connections[1] = null;
        }

        return result;
    }

    public void clear() {
        for (IConductor iConductor : contains) {
            map.remove(iConductor);
        }

        this.contains.clear();
    }

    public boolean appendConnection(IBaseComponent baseComponent) {
        boolean result = true;
//		if(baseComponent instanceof IConductor)
        if (contains.contains(baseComponent))
            return false;

        if (connections[0] == null)
            connections[0] = baseComponent;
        else if (connections[1] == null)
            connections[1] = baseComponent;
        else
            result = false;

        return result;
    }

    public IBaseComponent getConnection(int index) {
        return connections[index];
    }
}
