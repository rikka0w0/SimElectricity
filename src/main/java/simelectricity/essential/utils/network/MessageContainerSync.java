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
    private boolean toServer;
    private int windowID;
    private Object[] data;
    public MessageContainerSync() {
    }

    @SideOnly(Side.CLIENT)
    private MessageContainerSync(int windowID, Object[] data) {
		toServer = true;
        this.windowID = windowID;
        this.data = data;
    }
    private MessageContainerSync(Object[] data) {
		toServer = false;
		windowID = -1;
        this.data = data;
    }

    public static void sendToClient(EntityPlayerMP player, Object... data) {
        Essential.instance.networkChannel.sendTo(new MessageContainerSync(data), player);
    }

    @SideOnly(Side.CLIENT)
    public static void sendButtonClickEventToSever(Container clientContainer, int buttonID, boolean isCtrlPressed) {
        Essential.instance.networkChannel.sendToServer(new MessageContainerSync(clientContainer.windowId,
                new Object[]{MessageContainerSync.EVENT_BUTTON_CLICK, buttonID, isCtrlPressed}));
    }

    @SideOnly(Side.CLIENT)
    public static void sendDirectionSelectorClickEventToSever(Container clientContainer, EnumFacing direction, int mouseButton) {
        Essential.instance.networkChannel.sendToServer(new MessageContainerSync(clientContainer.windowId,
                new Object[]{MessageContainerSync.EVENT_DIRECTION_SELECT, direction, mouseButton}));
    }

    @SideOnly(Side.CLIENT)
    public static void sendToServer(Container clientContainer, Object... data) {
        Essential.instance.networkChannel.sendToServer(new MessageContainerSync(clientContainer.windowId, data));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.toServer);
        buf.writeInt(this.windowID);
        buf.writeByte(this.data.length);

        for (int i = 0; i < this.data.length; i++) {
            if (this.data[i].getClass() == Byte.class) {
                buf.writeByte(MessageContainerSync.TYPE_BYTE);
                buf.writeByte((Byte) this.data[i]);
            } else if (this.data[i].getClass() == Integer.class) {
                buf.writeByte(MessageContainerSync.TYPE_INT);
                buf.writeInt((Integer) this.data[i]);
            } else if (this.data[i].getClass() == Double.class) {
                buf.writeByte(MessageContainerSync.TYPE_DOUBLE);
                buf.writeDouble((Double) this.data[i]);
            } else if (this.data[i].getClass() == EnumFacing.class) {
                buf.writeByte(MessageContainerSync.TYPE_ENUMFACING);
                buf.writeByte(((EnumFacing) this.data[i]).ordinal());
            } else if (this.data[i].getClass() == Boolean.class) {
                buf.writeByte(MessageContainerSync.TYPE_BOOLEAN);
                buf.writeBoolean((Boolean) this.data[i]);
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
		this.toServer = buf.readBoolean();
		this.windowID = buf.readInt();
        int length = buf.readByte();
		this.data = new Object[length];

        for (int i = 0; i < this.data.length; i++) {
            switch (buf.readByte()) {
                case MessageContainerSync.TYPE_BYTE:
					this.data[i] = buf.readByte();
                    break;
                case MessageContainerSync.TYPE_INT:
					this.data[i] = buf.readInt();
                    break;
                case MessageContainerSync.TYPE_DOUBLE:
					this.data[i] = buf.readDouble();
                    break;
                case MessageContainerSync.TYPE_ENUMFACING:
					this.data[i] = EnumFacing.getFront(buf.readByte());
                    break;
                case MessageContainerSync.TYPE_BOOLEAN:
					this.data[i] = buf.readBoolean();
            }
        }

    }

    public static class Handler implements IMessageHandler<MessageContainerSync, IMessage> {
        @Override
        public IMessage onMessage(MessageContainerSync message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
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
                                    case MessageContainerSync.EVENT_CUSTOM:
                                        if (container instanceof ISECustomContainerEventHandler)
                                            ((ISECustomContainerEventHandler) container).onDataArrivedFromClient(data);
                                        break;
                                    case MessageContainerSync.EVENT_BUTTON_CLICK:
                                        if (container instanceof ISEButtonEventHandler)
                                            ((ISEButtonEventHandler) container).onButtonPressed((Integer) data[1], (Boolean) data[2]);
                                        break;
                                    case MessageContainerSync.EVENT_DIRECTION_SELECT:
                                        if (container instanceof ISEDirectionSelectorEventHandler)
                                            ((ISEDirectionSelectorEventHandler) container).onDirectionSelected((EnumFacing) data[1], (Integer) data[2]);
                                        break;
                                }
                            }
                        }//while()
                    }//run()
                });

            } else {
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
            }

            //Reply nothing
            return null;
        }

    }
}
