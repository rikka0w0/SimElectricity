package simElectricity.Common.Network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import simElectricity.API.EnergyTile.IEnergyTile;
import simElectricity.API.ISidedFacing;
import simElectricity.API.Util;

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
            value = Util.direction2Byte(((ISidedFacing) te).getFacing());
        } else if (type == 1) {
            value = Util.direction2Byte(((IEnergyTile) te).getFunctionalSide());
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

            ((ISidedFacing) te).setFacing(Util.byte2Direction(value));
            world.markBlockForUpdate(x, y, z);
        } else if (type == 1) {
            if (!(te instanceof IEnergyTile))
                return;

            ((IEnergyTile) te).setFunctionalSide(Util.byte2Direction(value));
            world.markBlockForUpdate(x, y, z);
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
    }
}
