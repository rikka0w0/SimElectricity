package simElectricity.Templates.Utils;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.Templates.SETemplate;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

/**
 * Update client GUI
 * </p>
 * Send data from server to client
 *
 */
public class MessageGui implements IMessage{
	public static void sendToGui(EntityPlayerMP player, byte eventID, Object... data){
		SETemplate.instance.networkChannel.sendTo(new MessageGui(eventID, data), player);
	}
	
	public static void sendToServer(TileEntity te, byte eventID, Object... data){
		SETemplate.instance.networkChannel.sendToServer(new MessageGui(te, eventID, data));
	}
	
	public final static byte TYPE_BYTE = 0;
	public final static byte TYPE_INT = 1;
	public final static byte TYPE_DOUBLE = 2;
	public final static byte TYPE_ENUMFACING = 3;
	public final static byte TYPE_BOOLEAN = 4;
	
	//MessageData
	private boolean isToServer;
	private byte eventID;
	private Object[] data;
	
	//C2S only
	private int x,y,z,dim;
	
	public MessageGui(){}
	
	private MessageGui (TileEntity te, byte eventID, Object[] data){
		this.isToServer = true;
		this.eventID = eventID;
		
		this.x = te.xCoord;
		this.y = te.yCoord;
		this.z = te.zCoord;
		this.dim = te.getWorldObj().provider.dimensionId;
		
		this.data = data;
	}
	
	private MessageGui (byte eventID, Object[] data){
		this.isToServer = false;
		this.eventID = eventID;
		
		this.data = data;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
    	buf.writeBoolean(isToServer);
    	if (isToServer){
    		buf.writeInt(x);
    		buf.writeInt(y);
    		buf.writeInt(z);
    		buf.writeInt(dim); 
    	}
		
    	buf.writeByte(eventID);
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
		isToServer = buf.readBoolean();
		if (isToServer){
    		x = buf.readInt();
    		y = buf.readInt();
    		z = buf.readInt();
    		dim = buf.readInt();
		}
		
		eventID = buf.readByte();
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
	public static class Handler implements IMessageHandler<MessageGui, IMessage>{
		@Override
		public IMessage onMessage(MessageGui message, MessageContext ctx) {
			if (ctx.side == Side.SERVER){
				//Server
				World world = ctx.getServerHandler().playerEntity.worldObj;
				if (world.provider.dimensionId != message.dim){
					//Wrong world
					return null;
				}
				
				TileEntity te = world.getTileEntity(message.x, message.y, message.z);
				if (te instanceof IGuiSyncHandler)
					((IGuiSyncHandler) te).onGuiEvent(message.eventID, message.data);
			}else{
				//Client
				Object screen = SETemplate.proxy.getCurrentGui();
				if (screen instanceof IGuiSyncHandler)
					((IGuiSyncHandler) screen).onGuiEvent(message.eventID, message.data);
			}
			return null;
		}
		
	}
	
}
