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
 * This represents a circuit component in the simulation.
 * <p/>
 * Can be a source or sink, determined by {@link #getOutputVoltage()}
 * Another important function is {@link #getResistance()},
 * it can represent the resistance of a load or the internal resistance of a generator.
 * <p/>
 * Usually, this interface should NOT implemented by a tileEntity, this interface can just represents subComponents of a {@link simElectricity.API.EnergyTile.IComplexTile} TileEntity
 * A normal machine or generator should implements {@link simElectricity.API.EnergyTile.IEnergyTile}
 */
public interface ICircuitComponent extends IBaseComponent {
    /**
     * Return 0 for sink(typically machines), other value for source(e.g generator)
     */
    float getOutputVoltage();
}
