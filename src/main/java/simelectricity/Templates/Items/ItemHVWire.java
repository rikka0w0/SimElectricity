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

package simelectricity.Templates.Items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.api.SEAPI;
import simelectricity.Templates.Common.ItemSE;
import simelectricity.Templates.Utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class ItemHVWire extends ItemSE {
    public static Map<EntityPlayer, int[]> lastCoordinates = new HashMap<EntityPlayer, int[]>();

    public ItemHVWire() {
        super();
        setUnlocalizedName("HVWire");
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {

            if (!(world.getTileEntity(x, y, z) instanceof ISEGridTile)) {
            	Utils.chat(player, EnumChatFormatting.RED + StatCollector.translateToLocal("sime.TgtNotTw") + EnumChatFormatting.RESET);
                return true;
            }

            if (!lastCoordinates.containsKey(player))
                lastCoordinates.put(player, new int[] { 0, -1, 0 });

            int[] lastCoordinate = lastCoordinates.get(player);

            if (lastCoordinate[1] == -1) {
                lastCoordinate[0] = x;
                lastCoordinate[1] = y;
                lastCoordinate[2] = z;

                Utils.chat(player, StatCollector.translateToLocal("sime.TwSelect"));
            } else {
                if (!(lastCoordinate[0] == x && lastCoordinate[1] == y && lastCoordinate[2] == z) &&
                        world.getTileEntity(lastCoordinate[0], lastCoordinate[1], lastCoordinate[2]) instanceof ISEGridTile) {

                    if (Math.pow(x - lastCoordinate[0], 2) + Math.pow(z - lastCoordinate[2], 2) < 16) {
                    	Utils.chat(player, EnumChatFormatting.RED + StatCollector.translateToLocal("sime.TwClose") + EnumChatFormatting.RESET);
                        return true;
                    }

                    TileEntity te1 = world.getTileEntity(lastCoordinate[0], lastCoordinate[1], lastCoordinate[2]);
                    TileEntity te2 = world.getTileEntity(x, y, z);

                    if (((ISEGridTile)te1).canConnect() && ((ISEGridTile)te2).canConnect()) {
                    	double resistance = (0.2 * Math.sqrt(te1.getDistanceFrom(x, y, z)));
                    	SEAPI.energyNetAgent.connectGridNode(world, x, y, z, lastCoordinate[0], lastCoordinate[1], lastCoordinate[2], resistance);
                    	Utils.chat(player, StatCollector.translateToLocal("sime.TwConnect"));
                    } else
                    	Utils.chat(player, StatCollector.translateToLocal("sime.ActionCancel"));
                } else
                	Utils.chat(player, StatCollector.translateToLocal("sime.ActionCancel"));

                lastCoordinate[0] = 0;
                lastCoordinate[1] = -1;
                lastCoordinate[2] = 0;
                lastCoordinates.put(player, lastCoordinate);
            }
        }
        return true;
    }
}
