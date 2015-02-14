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

package simElectricity.API;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import simElectricity.API.EnergyTile.IConductor;
import simElectricity.API.EnergyTile.IConnectable;
import simElectricity.API.EnergyTile.IEnergyTile;
import simElectricity.API.EnergyTile.ITransformer;

public class Util {
    public static final String MODID = "SimElectricity";
    public static final String NAME = "SimElectricity";

    public static boolean isSELoaded = false;
    ;

    /**
     * Creative Tab for SimElectricity project
     */
    public static CreativeTabs SETab;
    /**
     * Internal use only! [side][facing]
     */
    public static byte[][] sideAndFacingToSpriteOffset = new byte[][]{
            {3, 2, 0, 0, 0, 0},
            {2, 3, 1, 1, 1, 1},
            {1, 1, 3, 2, 5, 4},
            {0, 0, 2, 3, 4, 5},
            {4, 5, 4, 5, 3, 2},
            {5, 4, 5, 4, 2, 3}
    };

    /**
     * Post some text in chat box. (Other player cannot see it)
     */
    public static void chat(EntityPlayer player, String text) {
        player.addChatMessage(new ChatComponentText(text));
    }

    /**
     * Get a tileEntity on the given side of a tileEntity
     */
    public static TileEntity getTileEntityonDirection(TileEntity tileEntity, EnumFacing direction) {
        return tileEntity.getWorld().getTileEntity(new BlockPos(
                tileEntity.getPos().getX() + direction.getFrontOffsetX(),
                tileEntity.getPos().getY() + direction.getFrontOffsetY(),
                tileEntity.getPos().getZ() + direction.getFrontOffsetZ()));

    }

    //Facing and Rendering

    /**
     * Used by wires to find possible connections
     */
    public static boolean possibleConnection(TileEntity tileEntity, EnumFacing direction) {
        TileEntity ent = getTileEntityonDirection(tileEntity, direction);

        if (ent instanceof IConductor) {
            if (tileEntity instanceof IConductor) {
                if (((IConductor) ent).getColor() == 0 ||
                        ((IConductor) tileEntity).getColor() == 0 ||
                        ((IConductor) ent).getColor() == ((IConductor) tileEntity).getColor()) {
                    return true;
                }
            } else {
                return true;
            }

        } else if (ent instanceof IEnergyTile) {
            EnumFacing functionalSide = ((IEnergyTile) ent).getFunctionalSide();

            if (direction == functionalSide.getOpposite())
                return true;

        } else if (ent instanceof IConnectable) {
            if (((IConnectable) ent).canConnectOnSide(direction.getOpposite()))
                return true;
        } else if (ent instanceof ITransformer) {
            if (((ITransformer) ent).getPrimarySide() == direction.getOpposite() ||
                    ((ITransformer) ent).getSecondarySide() == direction.getOpposite())
                return true;
        }

        return false;
    }

    /**
     * Get the texture index for a given side with a rotation
     */
    public static int getTextureOnSide(int side, EnumFacing direction) {
        switch (direction) {
            case NORTH:
                return sideAndFacingToSpriteOffset[side][3];
            case SOUTH:
                return sideAndFacingToSpriteOffset[side][2];
            case WEST:
                return sideAndFacingToSpriteOffset[side][5];
            case EAST:
                return sideAndFacingToSpriteOffset[side][4];
            case UP:
                return sideAndFacingToSpriteOffset[side][0];
            case DOWN:
                return sideAndFacingToSpriteOffset[side][1];
            default:
                return 0;
        }
    }

    /**
     * Return which direction the player is looking at
     *
     * @param ignoreVertical return direction will ignore vertical directions(UP, DOWN) if this parameter set to true
     */
    public static EnumFacing getPlayerSight(EntityLivingBase player, boolean ignoreVertical) {
        int heading = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        int pitch = Math.round(player.rotationPitch);
        if (!ignoreVertical) {
            if (pitch >= 65)
                return EnumFacing.DOWN;  //1

            if (pitch <= -65)
                return EnumFacing.UP;    //0
        }
        switch (heading) {
            case 0:
                return EnumFacing.SOUTH; //2
            case 1:
                return EnumFacing.WEST;  //5
            case 2:
                return EnumFacing.NORTH; //3
            case 3:
                return EnumFacing.EAST;  //4
            default:
                return null;
        }
    }

    // Block/Item

    /**
     * @param name The name of the block.
     * @return The block or null if not found
     */
    public static Block getBlock(String name) {
        return GameRegistry.findBlock(MODID, name);
    }

    /**
     * @param name The name of the item.
     * @return The item or null if not found
     */
    public static Item getItem(String name) {
        return GameRegistry.findItem(MODID, name);
    }
}
