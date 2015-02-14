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


import net.minecraft.util.EnumFacing;

/**
 * This interface can represent a energy source or a energy sink
 * <p/>
 * e.g. a generator or a machine should implement this interface!
 * <p/>
 * See {@link simElectricity.API.EnergyTile.ICircuitComponent} for information about
 * {@link simElectricity.API.EnergyTile.ICircuitComponent#getOutputVoltage() getOutputVoltage()};
 */
public interface IEnergyTile extends ICircuitComponent {
    /**
     * Return a side that is designed to interact with energyNet
     */
    EnumFacing getFunctionalSide();

    /**
     * Called when the functional side is going to be set
     */
    void setFunctionalSide(EnumFacing newFunctionalSide);

    /**
     * Usually called by the wrench, to determine set or not
     */
    boolean canSetFunctionalSide(EnumFacing newFunctionalSide);
}