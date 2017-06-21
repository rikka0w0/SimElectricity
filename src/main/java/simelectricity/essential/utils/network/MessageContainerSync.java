package simelectricity.essential.utils.network;

import simelectricity.essential.Essential;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class MessageContainerSync implements IMessage{
	public static void sendToClient(EntityPlayerMP player, Object... data){
		Essential.instance.networkChannel.sendTo(new MessageContainerSync(false, data), player);
	}
	
	private final static byte TYPE_BYTE = 0;
	private final static byte TYPE_INT = 1;
	private final static byte TYPE_DOUBLE = 2;
	private final static byte TYPE_ENUMFACING = 3;
	private final static byte TYPE_BOOLEAN = 4;
	
	//MessageData
	private boolean toServer;
	private Object[] data;
	
	public MessageContainerSync(){}
	
	private MessageContainerSync (boolean toServer, Object[] data){
		this.toServer = toServer;
		
		this.data = data;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
    	buf.writeBoolean(toServer);
		
    	buf.writeByte(data.length);

    	for (int i = 0; i < data.length; i++){
    		if (data[i].getClass() == Byte.class){
        		buf.writeByte(TYPE_BYTE);
        		buf.writeByte((Byte) data[i]);
    		}
    		else if (data[i].getClass() == Integer.class){
        		buf.writeByte(TYPE_DOUBLE);
        		buf.writeInt((Integer) data[i]);
    		}
    		else if (data[i].getClass() == Double.class){
        		buf.writeByte(TYPE_DOUBLE);
        		buf.writeDouble((Double) data[i]);
    		}
    		else if (data[i].getClass() == ForgeDirection.class){
        		buf.writeByte(TYPE_ENUMFACING);
        		buf.writeByte(((ForgeDirection) data[i]).ordinal());
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

		data = new Object[buf.readByte()];

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
	    		data[i] = ForgeDirection.getOrientation(buf.readByte());
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
				
				
			}else{
				//Client
				Container invContainer = Essential.proxy.getClientPlayer().openContainer;
				
				if (invContainer instanceof ISEContainerUpdate)
					((ISEContainerUpdate)invContainer).onDataArrivedFromServer(message.data);
			}
			
			//Reply nothing
			return null;
		}
		
	}
}
