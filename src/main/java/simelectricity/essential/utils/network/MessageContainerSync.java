package simelectricity.essential.utils.network;

import java.util.Iterator;

import simelectricity.essential.Essential;

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

public class MessageContainerSync implements IMessage{
	public static void sendToClient(EntityPlayerMP player, Object... data){
		Essential.instance.networkChannel.sendTo(new MessageContainerSync(data), player);
	}
	
	@SideOnly(Side.CLIENT)
	public static void sendButtonClickEventToSever(Container clientContainer, int buttonID, boolean isCtrlPressed){
		Essential.instance.networkChannel.sendToServer(new MessageContainerSync(clientContainer.windowId,
				new Object[]{EVENT_BUTTON_CLICK, buttonID, isCtrlPressed}));
	}
	
	@SideOnly(Side.CLIENT)
	public static void sendDirectionSelectorClickEventToSever(Container clientContainer, EnumFacing direction, int mouseButton){
		Essential.instance.networkChannel.sendToServer(new MessageContainerSync(clientContainer.windowId,
				new Object[]{EVENT_DIRECTION_SELECT, direction, mouseButton}));
	}
	
	@SideOnly(Side.CLIENT)
	public static void sendToServer(Container clientContainer, Object... data){
		Essential.instance.networkChannel.sendToServer(new MessageContainerSync(clientContainer.windowId, data));
	}
	
	private final static byte EVENT_CUSTOM = 0;
	private final static byte EVENT_BUTTON_CLICK = 1;
	private final static byte EVENT_DIRECTION_SELECT = 2;
	
	private final static byte TYPE_BYTE = 0;
	private final static byte TYPE_INT = 1;
	private final static byte TYPE_DOUBLE = 2;
	private final static byte TYPE_ENUMFACING = 3;
	private final static byte TYPE_BOOLEAN = 4;
	
	//MessageData
	private boolean toServer;
	private int windowID;
	private Object[] data;
	
	public MessageContainerSync(){}
	
	@SideOnly(Side.CLIENT)
	private MessageContainerSync (int windowID, Object[] data){
		this.toServer = true;
		this.windowID = windowID;
		this.data = data;
	}
	
	private MessageContainerSync (Object[] data){
		this.toServer = false;
		this.windowID = -1;
		this.data = data;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
    	buf.writeBoolean(toServer);
    	buf.writeInt(windowID);
    	buf.writeByte(data.length);

    	for (int i = 0; i < data.length; i++){
    		if (data[i].getClass() == Byte.class){
        		buf.writeByte(TYPE_BYTE);
        		buf.writeByte((Byte) data[i]);
    		}
    		else if (data[i].getClass() == Integer.class){
        		buf.writeByte(TYPE_INT);
        		buf.writeInt((Integer) data[i]);
    		}
    		else if (data[i].getClass() == Double.class){
        		buf.writeByte(TYPE_DOUBLE);
        		buf.writeDouble((Double) data[i]);
    		}
    		else if (data[i].getClass() == EnumFacing.class){
        		buf.writeByte(TYPE_ENUMFACING);
        		buf.writeByte(((EnumFacing) data[i]).ordinal());
    		}    	
    		else if (data[i].getClass() == Boolean.class){
    			buf.writeByte(TYPE_BOOLEAN);
    			buf.writeBoolean((Boolean) data[i]);
    		}
    	}
	}
    
	@Override
	public void fromBytes(ByteBuf buf) {
		toServer = buf.readBoolean();
		windowID = buf.readInt();
		int length = buf.readByte();
		data = new Object[length];

		for (int i = 0; i < data.length; i++){
	    	switch (buf.readByte()){
	    	case TYPE_BYTE:
	    		data[i] = buf.readByte();
	    		break;
	    	case TYPE_INT:
	    		data[i] = buf.readInt();
	    		break;
	    	case TYPE_DOUBLE:
	    		data[i] = buf.readDouble();
	    		break;
	    	case TYPE_ENUMFACING:
	    		data[i] = EnumFacing.getFront(buf.readByte());
	    		break;
	    	case TYPE_BOOLEAN:
	    		data[i] = buf.readBoolean();
	    	}
		}

	}
	
	/**
	 * 
	 *	This message can only be proceed on the client side
	 *
	 */
	public static class Handler implements IMessageHandler<MessageContainerSync, IMessage>{

		@Override
		public IMessage onMessage(MessageContainerSync message, MessageContext ctx) {
			if (ctx.side == Side.SERVER){
				//Server
				final MinecraftServer server = ctx.getServerHandler().playerEntity.mcServer;
				final int windowID = message.windowID;
				final Object[] data = message.data;
				server.addScheduledTask(new Runnable(){
					@Override
					public void run() {
						Iterator<EntityPlayerMP> playerListIterator = server.getPlayerList().getPlayers().iterator();
						while (playerListIterator.hasNext()){
							EntityPlayerMP player = playerListIterator.next();
							
							if (player.openContainer.windowId == windowID){
								final Container container = player.openContainer;
								
								switch ((Byte)data[0]){
								case EVENT_CUSTOM:
									if (container instanceof ISECustomContainerEventHandler)
										((ISECustomContainerEventHandler) container).onDataArrivedFromClient(data);	
									break;
								case EVENT_BUTTON_CLICK:
									if (container instanceof ISEButtonEventHandler)
										((ISEButtonEventHandler) container).onButtonPressed((Integer)data[1], (Boolean)data[2]);
									break;
								case EVENT_DIRECTION_SELECT:
									if (container instanceof ISEDirectionSelectorEventHandler)
										((ISEDirectionSelectorEventHandler) container).onDirectionSelected((EnumFacing)data[1], (Integer)data[2]);
									break;
								}
							}
						}//while()
					}//run()
				});
				
			}else{
				final Object[] payload = message.data;
				
				//Client
				Essential.proxy.getClientThread().addScheduledTask(new Runnable()
				{
					  public void run() {
						  Container invContainer = Essential.proxy.getClientPlayer().openContainer;
						  if (invContainer instanceof ISEContainerUpdate)
								((ISEContainerUpdate)invContainer).onDataArrivedFromServer(payload);
					  }
					});
			}
			
			//Reply nothing
			return null;
		}
		
	}
}
