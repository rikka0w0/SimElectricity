package simElectricity.Network;

import java.lang.reflect.Field;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**This packet performs server->client side synchronization~*/
public class PacketTileEntityFieldUpdate extends AbstractPacket {
	public PacketTileEntityFieldUpdate(){}

	
	int x,z,hash;
	short y;
	Short type,fieldLength;
	String field;
	Object value;
	
	public PacketTileEntityFieldUpdate(TileEntity te, String _field){		
		if (te==null)
			return;
		
		if (te.getWorldObj()==null)
			return;
		
		x=te.xCoord;
		y=(short) te.yCoord;
		z=te.zCoord;
		field=_field;
		hash=te.getClass().hashCode();
		
		Field f;
		try {
			f = te.getClass().getField(field);
			if(f.getType()==boolean.class){
				type=0;
				value=f.getBoolean(te);
			}			
			else if(f.getType()==int.class){
				type=1;
				value=f.getInt(te);
			}
			else{
				System.out.println(te.toString()+" is trying synchronous a unknown type field: "+_field);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		buffer.writeInt(x);
		buffer.writeShort(y);
		buffer.writeInt(z);
		buffer.writeInt(hash);
		buffer.writeShort(type);
		buffer.writeShort(field.length());
		for(char c:field.toCharArray())
			buffer.writeChar(c);
		
		switch(type){
		case 0:
			buffer.writeBoolean((Boolean) value);
			break;
		case 1:
			buffer.writeInt((Integer) value);
			break;			
		}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		x = buffer.readInt();
        y = buffer.readShort();
        z = buffer.readInt();
        hash=buffer.readInt();
        type=buffer.readShort();
        fieldLength=buffer.readShort();
        
        field="";
		for (int i=0;i<fieldLength;i++)
			field+=buffer.readChar();
		
		switch(type){
		case 0:
			value=buffer.readBoolean();
			break;
		case 1:
			value=buffer.readInt();
			break;			
		}
	}

	
	@Override
	public void handleClientSide(EntityPlayer player) {
		System.out.print(field);
		System.out.print("=");
		System.out.println(value);
		
		try{
			World world = player.worldObj;
			TileEntity te = world.getTileEntity(x, y, z);
			
			if (te == null) 
				return;
			if (te.getClass().hashCode()!=hash)
				return;
			
			Field f=te.getClass().getField(field);
			switch (type){
			case 0:
				f.setBoolean(te,(Boolean) value);
				break;
			case 1:
				f.setInt(te,(Integer) value);
				break;				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		//Do nothing here
	}

}
