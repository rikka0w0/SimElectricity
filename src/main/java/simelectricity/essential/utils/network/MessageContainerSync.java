package simelectricity.essential.utils.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.Essential;

import java.util.Iterator;

public class MessageContainerSync implements IMessage {
    private static final byte EVENT_CUSTOM = 0;
    private static final byte EVENT_BUTTON_CLICK = 1;
    private static final byte EVENT_DIRECTION_SELECT = 2;
    private static final byte TYPE_BYTE = 0;
    private static final byte TYPE_INT = 1;
    private static final byte TYPE_DOUBLE = 2;
    private static final byte TYPE_ENUMFACING = 3;
    private static final byte TYPE_BOOLEAN = 4;
    //MessageData
    private int windowID;
    private Object[] data;

    public MessageContainerSync() {
    }

    @SideOnly(Side.CLIENT)
    private MessageContainerSync(int windowID, Object[] data) {
        this.windowID = windowID;
        this.data = data;
    }

    private MessageContainerSync(Object[] data) {
        windowID = -1;
        this.data = data;
    }

    public static void sendToClient(EntityPlayerMP player, Object... data) {
        Essential.instance.networkChannel.sendTo(new MessageContainerSync(data), player);
    }

    @SideOnly(Side.CLIENT)
    public static void sendButtonClickEventToSever(Container clientContainer, int buttonID, boolean isCtrlPressed) {
        Essential.instance.networkChannel.sendToServer(new MessageContainerSync(clientContainer.windowId,
                new Object[]{EVENT_BUTTON_CLICK, buttonID, isCtrlPressed}));
    }

    @SideOnly(Side.CLIENT)
    public static void sendDirectionSelectorClickEventToSever(Container clientContainer, EnumFacing direction, int mouseButton) {
        Essential.instance.networkChannel.sendToServer(new MessageContainerSync(clientContainer.windowId,
                new Object[]{EVENT_DIRECTION_SELECT, direction, mouseButton}));
    }

    @SideOnly(Side.CLIENT)
    public static void sendToServer(Container clientContainer, Object... data) {
        Essential.instance.networkChannel.sendToServer(new MessageContainerSync(clientContainer.windowId, data));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.windowID);
        buf.writeByte(this.data.length);

        for (int i = 0; i < this.data.length; i++) {
            if (this.data[i].getClass() == Byte.class) {
                buf.writeByte(TYPE_BYTE);
                buf.writeByte((Byte) this.data[i]);
            } else if (this.data[i].getClass() == Integer.class) {
                buf.writeByte(TYPE_INT);
                buf.writeInt((Integer) this.data[i]);
            } else if (this.data[i].getClass() == Double.class) {
                buf.writeByte(TYPE_DOUBLE);
                buf.writeDouble((Double) this.data[i]);
            } else if (this.data[i].getClass() == EnumFacing.class) {
                buf.writeByte(TYPE_ENUMFACING);
                buf.writeByte(((EnumFacing) this.data[i]).ordinal());
            } else if (this.data[i].getClass() == Boolean.class) {
                buf.writeByte(TYPE_BOOLEAN);
                buf.writeBoolean((Boolean) this.data[i]);
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.windowID = buf.readInt();
        int length = buf.readByte();
        this.data = new Object[length];

        for (int i = 0; i < this.data.length; i++) {
            switch (buf.readByte()) {
                case TYPE_BYTE:
                    this.data[i] = buf.readByte();
                    break;
                case TYPE_INT:
                    this.data[i] = buf.readInt();
                    break;
                case TYPE_DOUBLE:
                    this.data[i] = buf.readDouble();
                    break;
                case TYPE_ENUMFACING:
                    this.data[i] = EnumFacing.getFront(buf.readByte());
                    break;
                case TYPE_BOOLEAN:
                    this.data[i] = buf.readBoolean();
            }
        }

    }

    //This class have to be visible to the dedicated server even the server doesn't need it at all
    public static class HandlerClient implements IMessageHandler<MessageContainerSync, IMessage> {
        @Override
        public IMessage onMessage(MessageContainerSync message, MessageContext ctx) {
            Object[] payload = message.data;

            //Client
            Essential.proxy.getClientThread().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    Container invContainer = Essential.proxy.getClientPlayer().openContainer;
                    if (invContainer instanceof ISEContainerUpdate)
                        ((ISEContainerUpdate) invContainer).onDataArrivedFromServer(payload);
                }
            });

            //Reply nothing
            return null;
        }
    }

    public static class HandlerServer implements IMessageHandler<MessageContainerSync, IMessage> {
        @Override
        public IMessage onMessage(MessageContainerSync message, MessageContext ctx) {
            //Server
            MinecraftServer server = ctx.getServerHandler().player.mcServer;
            int windowID = message.windowID;
            Object[] data = message.data;

            //Make sure the actual modification is done on the server-thread.
            server.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    Iterator<EntityPlayerMP> playerListIterator = server.getPlayerList().getPlayers().iterator();
                    while (playerListIterator.hasNext()) {
                        EntityPlayerMP player = playerListIterator.next();

                        if (player.openContainer.windowId == windowID) {
                            Container container = player.openContainer;

                            switch ((Byte) data[0]) {
                                case EVENT_CUSTOM:
                                    if (container instanceof ISECustomContainerEventHandler)
                                        ((ISECustomContainerEventHandler) container).onDataArrivedFromClient(data);
                                    break;
                                case EVENT_BUTTON_CLICK:
                                    if (container instanceof ISEButtonEventHandler)
                                        ((ISEButtonEventHandler) container).onButtonPressed((Integer) data[1], (Boolean) data[2]);
                                    break;
                                case EVENT_DIRECTION_SELECT:
                                    if (container instanceof ISEDirectionSelectorEventHandler)
                                        ((ISEDirectionSelectorEventHandler) container).onDirectionSelected((EnumFacing) data[1], (Integer) data[2]);
                                    break;
                            }
                        }
                    }//while()
                }//run()
            });

            //Reply nothing
            return null;
        }
    }

}
