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

package simElectricity.Common.Network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkWatchEvent;
import simElectricity.API.INetworkEventHandler;
import simElectricity.SimElectricity;

import java.util.ArrayList;

public class NetworkManager {
    public NetworkManager() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Update a client tileEntity field from the server
     */
    public static void updateTileEntityFields(TileEntity tileEntity, String[] fields) {
    	SimElectricity.instance.networkChannel.sendToDimension(new MessageTileEntityUpdate(tileEntity, fields), tileEntity.getWorldObj().provider.dimensionId);
    }

    /**
     * Update a client tileEntity field from the server
     */
    public static void updateTileEntityFieldsToServer(TileEntity tileEntity, String[] fields) {
    	SimElectricity.instance.networkChannel.sendToServer(new MessageTileEntityUpdate(tileEntity, fields));
    }

    /**
     * Attempt to update a tileEntity's network fields
     */
    public static void updateNetworkFields(TileEntity tileEntity){
    	if (!(tileEntity instanceof INetworkEventHandler))
    		return;

    	INetworkEventHandler networkEventHandler = (INetworkEventHandler) tileEntity;
    	ArrayList<String> fields = new ArrayList<String>();
    	networkEventHandler.addNetworkFields(fields);

    	updateTileEntityFields(tileEntity, fields.toArray(new String[1]));
    }

    //When a player see the chunk, update facing, functionalside, wire rendering
    @SubscribeEvent
    public void onChunkWatchEvent(ChunkWatchEvent.Watch event) {
        Chunk chunk = event.player.worldObj.getChunkFromChunkCoords(event.chunk.chunkXPos, event.chunk.chunkZPos);

        for (Object tileEntity : chunk.chunkTileEntityMap.values()) {
            TileEntity te = (TileEntity) tileEntity;
            updateNetworkFields(te);

            //if (te instanceof IConductor) {
            	//Send onNeighborBlockChange to IConductors, do reRender here
            	//te.getWorldObj().getBlock(te.xCoord, te.yCoord, te.zCoord).onNeighborBlockChange(te.getWorldObj(),te.xCoord, te.yCoord, te.zCoord, null);
                //Be extremely careful when update something on the edge of the chunk
                //if (te.xCoord % 16 == 0 || te.xCoord % 16 == 1 || te.xCoord % 16 == 15 ||
                //        te.zCoord % 16 == 0 || te.zCoord % 16 == 1 || te.zCoord % 16 == 15)
                //    te.getWorldObj().notifyBlocksOfNeighborChange(te.xCoord, te.yCoord, te.zCoord, null);
            //}
        }
    }


 }
