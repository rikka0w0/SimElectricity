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

package simelectricity.api;


public interface ISEEnergyNetUpdateHandler {
    /**
     * This function will be called as soon as the EnergyNet finishes simulation
     * Suitable for TileEntities which implement any of the following:<br>
     * ISECableTile, ISEGridTile and ISEMachineTile<br>
     * Warning: this function is always called from the EnergyNet thread, so do not directly interact with
     * Blocks, TileEntities or anything in the world.<br>
     * Inappropriate multi-threading can lead to random weirdness in the game and
     * it be can extremely hard to locate the problem.<br>
     * <br>
     * The recommended solution is to schedule a task to the server's queue so that
     * the server thread will execute them.<br>
     * <br>
     * Since this method is server-only, the world object in TileEntities must be WorldServer,
     * (Remember EnergyNet threads are server threads, clients can not have them!),
     * so simply cast the world object into WorldServer and call WorldServer.addScheduledTask()
     */
    void onEnergyNetUpdate();
}
