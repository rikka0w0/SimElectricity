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

package simElectricity.Common;

import java.util.Arrays;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;
import simElectricity.API.SEAPI;
import simElectricity.API.EnergyTile.ISEConductor;
import simElectricity.API.Internal.ISEUtils;

/**
 * Created by <Meow J> on 8/6/2014.
 *
 * @author Meow J
 */
public class SEUtils implements ISEUtils{
    public static final String MODID = "SimElectricity";
    public static final String NAME = "SimElectricity";
	
	@Override
	public String GetModName() {
		return MODID;
	}
    
    //Facing and Rendering
	@Override
    public int getTextureOnSide(int side, ForgeDirection direction) {
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

	@Override
    public ForgeDirection getPlayerSight(EntityLivingBase player, boolean ignoreVertical) {
        int heading = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        int pitch = Math.round(player.rotationPitch);
        if (!ignoreVertical) {
            if (pitch >= 65)
                return ForgeDirection.DOWN;  //1

            if (pitch <= -65)
                return ForgeDirection.UP;    //0
        }
        switch (heading) {
            case 0:
                return ForgeDirection.SOUTH; //2
            case 1:
                return ForgeDirection.WEST;  //5
            case 2:
                return ForgeDirection.NORTH; //3
            case 3:
                return ForgeDirection.EAST;  //4
            default:
                return null;
        }
    }

    /**
     * Internal use only! [side][facing]
     */
    private static byte[][] sideAndFacingToSpriteOffset = new byte[][] {
            { 3, 2, 0, 0, 0, 0 },
            { 2, 3, 1, 1, 1, 1 },
            { 1, 1, 3, 2, 5, 4 },
            { 0, 0, 2, 3, 4, 5 },
            { 4, 5, 4, 5, 3, 2 },
            { 5, 4, 5, 4, 2, 3 }
    };
	
    @Override
    public void chat(EntityPlayer player, String text) {
        player.addChatMessage(new ChatComponentText(text));
    }
    
    @Override
    public TileEntity getTileEntityonDirection(TileEntity tileEntity, ForgeDirection direction) {
        return tileEntity.getWorldObj().getTileEntity(
                tileEntity.xCoord + direction.offsetX,
                tileEntity.yCoord + direction.offsetY,
                tileEntity.zCoord + direction.offsetZ);
    }
	

    /**
     * @param tileEntity       tileEntity
     * @param defaultDirection default direction
     *
     * @return valid conductor direction. If there is no conductor nearby, return default direction.
     */
    public ForgeDirection autoConnect(TileEntity tileEntity, ForgeDirection defaultDirection) {
        return autoConnect(tileEntity, defaultDirection, new ForgeDirection[] { });
    }

    /**
     * Exception version of auto-facing
     *
     * @param tileEntity       tileEntity
     * @param defaultDirection default direction
     * @param exception        exception direction
     *
     * @return valid conductor direction. If there is no conductor nearby, return default direction
     *
     * @see simElectricity.Common.Blocks.BlockSwitch
     */
    public ForgeDirection autoConnect(TileEntity tileEntity, ForgeDirection defaultDirection, ForgeDirection exception) {
        return autoConnect(tileEntity, defaultDirection, new ForgeDirection[] { exception });
    }

    /**
     * Exceptions array version of auto-facing
     *
     * @param tileEntity       tileEntity
     * @param defaultDirection default direction
     * @param exceptions       exception directions array
     *
     * @return valid conductor direction. If there is no conductor nearby, return default direction.
     */
    public ForgeDirection autoConnect(TileEntity tileEntity, ForgeDirection defaultDirection, ForgeDirection[] exceptions) {
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if (tileEntity.getWorldObj().getTileEntity(tileEntity.xCoord + direction.offsetX,
                    tileEntity.yCoord + direction.offsetY,
                    tileEntity.zCoord + direction.offsetZ) instanceof ISEConductor
                    && !Arrays.asList(exceptions).contains(direction))
                return direction;
        }
        return defaultDirection;
    }
    
    
    public static void logInfo(Object object) {
        FMLLog.log(NAME, Level.INFO, "[SimElectricity] " + String.valueOf(object));
    }

    public static void logWarn(Object object) {
        FMLLog.log(NAME, Level.WARN, "[SimElectricity] " + String.valueOf(object));
    }

    public static void logError(Object object) {
        FMLLog.log(NAME, Level.ERROR, "[SimElectricity] " + String.valueOf(object));
    }

    public static void logFatal(Object object) {
        FMLLog.log(NAME, Level.FATAL, "[SimElectricity] " + String.valueOf(object));
    }
}