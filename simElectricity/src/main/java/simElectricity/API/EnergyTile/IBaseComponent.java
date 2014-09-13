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

package simElectricity.API.EnergyTile;

/**
 * This interface is the base of components that participate in the simulation of energyNet,
 * but a normal simElectricity machine should not implement this interface >_<
 * See {@link simElectricity.API.EnergyTile.ICircuitComponent}, {@link simElectricity.API.EnergyTile.IEnergyTile} and {@link simElectricity.API.EnergyTile.IConductor} for more information
 * <p/>
 * Detailed instruction is available on
 * <a href="https://github.com/RoyalAliceAcademyOfSciences/SimElectricity/wiki">the wiki of simElectricity</a>
 */
public interface IBaseComponent{
    /**
     * Return the resistance of the machine
     * or
     * the internal resistance of the battery of the energy sink.
     * <p/>
     * NEVER return 0 (0 will crash the EnergyNet!),
     * but for {@link simElectricity.API.EnergyTile.IManualJunction}, 0 is allowed and mean something else, see {@link simElectricity.API.EnergyTile.IManualJunction} for further details
     * <p/>
     * Tips:
     * For a energy sink, the smaller resistance it has, the more energy it will consume.
     * For a conductor and energy source, the smaller resistance it has, the lower voltage drop it will have (better performance)
     */
    double getResistance();
}
