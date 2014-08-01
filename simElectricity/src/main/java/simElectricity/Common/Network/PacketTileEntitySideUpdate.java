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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.EnergyTile.IEnergyTile;
import simElectricity.API.ISidedFacing;

/**
 * This packet performs server<->client side synchronization for tileEntity fields~
 */
public class PacketTileEntitySideUpdate extends AbstractPacket {
    int x, z;
    short y;
    byte value, type;

    public PacketTileEntitySideUpdate() {
    }

    /**
     * type: 0-facing 1-functionalSide
     */
    public PacketTileEntitySideUpdate(TileEntity te, byte _type) {
        if (te == null)
            return;

        if (te.getWorldObj() == null)
            return;

        x = te.xCoord;
        y = (short) te.yCoord;
        z = te.zCoord;

        type = _type;

        if (type == 0) {
            value = (byte) ((ISidedFacing) te).getFacing().ordinal();
        } else if (type == 1) {
            value = (byte) ((IEnergyTile) te).getFunctionalSide().ordinal();
        } else {
            System.out.println("TileEntity " + te.toString() + " is trying to update its facing/functional side, but it's not an instance of ISidedFacing nor IEnergyTile, this must be a bug!");
        }
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        buffer.writeInt(x);
        buffer.writeShort(y);
        buffer.writeInt(z);
        buffer.writeByte(value);
        buffer.writeByte(type);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        x = buffer.readInt();
        y = buffer.readShort();
        z = buffer.readInt();
        value = buffer.readByte();
        type = buffer.readByte();
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        World world = player.worldObj;
        TileEntity te = world.getTileEntity(x, y, z);

        if (te == null)
            return;
        if (!world.isRemote)  //Client processing only!
            return;


        if (type == 0) {
            if (!(te instanceof ISidedFacing))
                return;

            ((ISidedFacing) te).setFacing(ForgeDirection.getOrientation(value));
            world.markBlockForUpdate(x, y, z);
        } else if (type == 1) {
            if (!(te instanceof IEnergyTile))
                return;

            ((IEnergyTile) te).setFunctionalSide(ForgeDirection.getOrientation(value));
            world.markBlockForUpdate(x, y, z);
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
    }
}
