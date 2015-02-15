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

package simElectricity.Common.Items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import simElectricity.API.Common.Items.ItemSE;
import simElectricity.API.IHVTower;
import simElectricity.API.Network;
import simElectricity.API.Util;

import java.util.HashMap;
import java.util.Map;

public class ItemHVWire extends ItemSE {
    public static Map<EntityPlayer, int[]> lastCoordinates = new HashMap<EntityPlayer, int[]>();

    public ItemHVWire() {
        super();
        setUnlocalizedName("hvwire");
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {

            if (!(world.getTileEntity(pos) instanceof IHVTower)) {
                Util.chat(player, EnumChatFormatting.RED + StatCollector.translateToLocal("sime.TgtNotTw") + EnumChatFormatting.RESET);
                return true;
            }

            if (!lastCoordinates.containsKey(player))
                lastCoordinates.put(player, new int[]{0, -1, 0});

            int[] lastCoordinate = lastCoordinates.get(player);

            if (lastCoordinate[1] == -1) {
                lastCoordinate[0] = pos.getX();
                lastCoordinate[1] = pos.getY();
                lastCoordinate[2] = pos.getZ();

                Util.chat(player, StatCollector.translateToLocal("sime.TwSelect"));
            } else {
                if (!(lastCoordinate[0] == pos.getX() && lastCoordinate[1] == pos.getY() && lastCoordinate[2] == pos.getZ()) &&
                        world.getTileEntity(new BlockPos(lastCoordinate[0], lastCoordinate[1], lastCoordinate[2])) instanceof IHVTower) {

                    if (Math.pow(pos.getX() - lastCoordinate[0], 2) + Math.pow(pos.getZ() - lastCoordinate[2], 2) < 64) {
                        Util.chat(player, EnumChatFormatting.RED + StatCollector.translateToLocal("sime.TwClose") + EnumChatFormatting.RESET);
                        return true;
                    }

                    IHVTower tower1 = (IHVTower) world.getTileEntity(new BlockPos(lastCoordinate[0], lastCoordinate[1], lastCoordinate[2]));
                    IHVTower tower2 = (IHVTower) world.getTileEntity(pos);

                    if (tower1.hasVacant() && tower2.hasVacant()) {
                        tower1.addNeighbor((TileEntity) tower2);
                        tower2.addNeighbor((TileEntity) tower1);

                        Network.updateTileEntityNBT((TileEntity) tower1);
                        Network.updateTileEntityNBT((TileEntity) tower2);

                        Util.chat(player, StatCollector.translateToLocal("sime.TwConnect"));
                    } else
                        Util.chat(player, StatCollector.translateToLocal("sime.ActionCancel"));
                } else
                    Util.chat(player, StatCollector.translateToLocal("sime.ActionCancel"));

                lastCoordinate[0] = 0;
                lastCoordinate[1] = -1;
                lastCoordinate[2] = 0;
                lastCoordinates.put(player, lastCoordinate);
            }
        }

        return true;
    }
}
