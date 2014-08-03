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
 * A wire should implements this interface, the getResistance() should return the resistance(Ohm) of a block of wire, see SimElectricity for more information
 */
public interface IConductor extends IBaseComponent {
    /**
     * Return the color of the wire
     * <p/>
     * 0 will allow any other wires connect to this wire
     * <p/>
     * Any other values will only connect to the certain value or 0
     */
    int getColor();
}
