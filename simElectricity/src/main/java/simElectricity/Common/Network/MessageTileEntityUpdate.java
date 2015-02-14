package simElectricity.Common.Network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import simElectricity.API.EnergyTile.IEnergyTile;
import simElectricity.API.INetworkEventHandler;
import simElectricity.API.ISidedFacing;
import simElectricity.Common.SEUtils;
import simElectricity.SimElectricity;

import java.lang.reflect.Field;

public class MessageTileEntityUpdate implements IMessage {
    private int xCoord, yCoord, zCoord, dimensionID,
            fieldsCount; //-1:Facing, -2:FunctionalSide, 0: nothing to update, >1:fields
    private String[] fields;
    private short[] types;
    private Object[] values;

    public MessageTileEntityUpdate() {
    }

    public MessageTileEntityUpdate(TileEntity te, EnumFacing direction, boolean isFacing) {
        initHead(te);

        this.fieldsCount = (byte) (isFacing ? -1 : -2);

        this.values = new EnumFacing[]{direction};
    }

    public MessageTileEntityUpdate(TileEntity te, String[] fields) {
        initHead(te);

        if (fields.length == 1 && fields[0] == null)
            this.fieldsCount = 0;
        else
            this.fieldsCount = fields.length;

        if (this.fieldsCount == 0) {
            SEUtils.logWarn("No fields to be update! This might be a bug!");
            return;
        }

        this.fields = fields;
        this.types = new short[fieldsCount];
        this.values = new Object[fieldsCount];
        try {

            for (int i = 0; i < fieldsCount; i++) {
                Field f = te.getClass().getField(fields[i]);

                values[i] = f.get(te);

                if (f.getType() == boolean.class) {
                    types[i] = 0;
                } else if (f.getType() == int.class) {
                    types[i] = 1;
                } else if (f.getType() == float.class) {
                    types[i] = 2;
                } else if (f.getType() == double.class) {
                    types[i] = 3;
                } else if (f.getType() == String.class) {
                    types[i] = 4;
                } else if (f.getType() == boolean[].class) {
                    types[i] = 5;
                } else if (f.getType() == int[].class) {
                    types[i] = 6;
                } else if (f.getType() == EnumFacing.class) {
                    types[i] = 7;
                } else {
                    types[i] = -1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initHead(TileEntity te) {
        if (te == null)
            return;

        if (te.getWorld() == null)
            return;

        this.xCoord = te.getPos().getX();
        this.yCoord = te.getPos().getY();
        this.zCoord = te.getPos().getZ();
        this.dimensionID = te.getWorld().provider.getDimensionId();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(xCoord);
        buf.writeInt(yCoord);
        buf.writeInt(zCoord);
        buf.writeInt(dimensionID);
        buf.writeInt(fieldsCount);

        if (fieldsCount == -1 || fieldsCount == -2) {
            buf.writeByte(((EnumFacing) values[0]).ordinal());
        }

        for (int i = 0; i < fieldsCount; i++) {
            buf.writeShort(types[i]);
            ByteBufUtils.writeUTF8String(buf, fields[i]);
            switch (types[i]) {
                case 0:
                    buf.writeBoolean((Boolean) values[i]);
                    break;
                case 1:
                    buf.writeInt((Integer) values[i]);
                    break;
                case 2:
                    buf.writeFloat((Float) values[i]);
                    break;
                case 3:
                    buf.writeDouble((Double) values[i]);
                    break;
                case 4:
                    ByteBufUtils.writeUTF8String(buf, (String) values[i]);
                    break;
                case 5:
                    buf.writeInt(((boolean[]) values[i]).length);
                    for (boolean j : (boolean[]) values[i]) {
                        buf.writeBoolean(j);
                    }
                    break;
                case 6:
                    buf.writeInt(((int[]) values[i]).length);
                    for (int j : (int[]) values[i]) {
                        buf.writeInt(j);
                    }
                    break;
                case 7:
                    buf.writeByte(((EnumFacing) values[i]).ordinal());
                    break;
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        xCoord = buf.readInt();
        yCoord = buf.readInt();
        zCoord = buf.readInt();
        dimensionID = buf.readInt();
        fieldsCount = buf.readInt();

        if (fieldsCount > 0) {
            fields = new String[fieldsCount];
            types = new short[fieldsCount];
            values = new Object[fieldsCount];

            for (int i = 0; i < fieldsCount; i++) {
                types[i] = buf.readShort();
                fields[i] = ByteBufUtils.readUTF8String(buf);
                switch (types[i]) {
                    case 0:
                        values[i] = buf.readBoolean();
                        break;
                    case 1:
                        values[i] = buf.readInt();
                        break;
                    case 2:
                        values[i] = buf.readFloat();
                        break;
                    case 3:
                        values[i] = buf.readDouble();
                        break;
                    case 4:
                        values[i] = ByteBufUtils.readUTF8String(buf);
                        break;
                    case 5:
                        boolean[] arrayBoolean = new boolean[buf.readInt()];
                        for (int j = 0; j < arrayBoolean.length; j++) {
                            arrayBoolean[j] = buf.readBoolean();
                        }
                        values[i] = arrayBoolean;
                        break;
                    case 6:
                        int[] arrayInt = new int[buf.readInt()];
                        for (int j = 0; j < arrayInt.length; j++) {
                            arrayInt[j] = buf.readInt();
                        }
                        values[i] = arrayInt;
                        break;
                    case 7:
                        values[i] = EnumFacing.getFront(buf.readByte());
                        break;
                }
            }
        } else {
            if (fieldsCount == -1 || fieldsCount == -2) {
                fields = new String[0];
                types = new short[0];
                values = new EnumFacing[]{EnumFacing.getFront(buf.readByte())};
            }
        }
    }


    public static class Handler implements IMessageHandler<MessageTileEntityUpdate, IMessage> {
        @Override
        public IMessage onMessage(MessageTileEntityUpdate message, MessageContext ctx) {
            World world;

            if (ctx.side == Side.CLIENT) {
                world = SimElectricity.proxy.getClientWorld();
            } else {
                world = ctx.getServerHandler().playerEntity.worldObj;
            }

            if (world.provider.getDimensionId() != message.dimensionID) {
                SEUtils.logWarn("An dimensionID mismatch error occurred during sync! This could be an error");
                return null;
            }

            TileEntity te = world.getTileEntity(new BlockPos(message.xCoord, message.yCoord, message.zCoord));

            if (te == null)
                return null;

            //Set value to variables
            try {
                for (int i = 0; i < message.fieldsCount; i++) {
                    Field f = te.getClass().getField(message.fields[i]);
                    f.set(te, message.values[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Update facing or functionalSide
            if (message.fieldsCount == -1) {
                ((ISidedFacing) te).setFacing((EnumFacing) message.values[0]);
                world.markBlockForUpdate(new BlockPos(message.xCoord, message.yCoord, message.zCoord));
            } else if (message.fieldsCount == -2) {
                ((IEnergyTile) te).setFunctionalSide((EnumFacing) message.values[0]);
                world.markBlockForUpdate(new BlockPos(message.xCoord, message.yCoord, message.zCoord));
            }

            //Fire onFieldUpdate event
            if (te instanceof INetworkEventHandler)
                ((INetworkEventHandler) te).onFieldUpdate(message.fields, message.values);

            return null;
        }
    }

}
