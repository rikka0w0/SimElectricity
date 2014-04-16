package simElectricity.Network;

import java.lang.reflect.Field;

import simElectricity.API.Util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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
			if(f.getType()==boolean.class){   //Boolean
				type=0;
				value=f.getBoolean(te);
			}			
			else if(f.getType()==int.class){  //Integer
				type=1;
				value=f.getInt(te);
			}
			else if(f.getType()==ForgeDirection.class){  //ForgeDirection
				type=2;
				value=Util.direction2Byte((ForgeDirection)f.get(te));
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
		case 2:
			buffer.writeByte((Byte)value);
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
		case 2:
			value=buffer.readByte();
			break;
		}
	}

	
	@Override
	public void handleClientSide(EntityPlayer player) {
		//System.out.print(field);
		//System.out.print("=");
		//System.out.println(value);
		
		try{
			World world = player.worldObj;
			TileEntity te = world.getTileEntity(x, y, z);
			
			if (te == null) 
				return;
			if (te.getClass().hashCode()!=hash)
				return;
			if(!world.isRemote)
				return;
			
			Field f=te.getClass().getField(field);
			switch (type){
			case 0:
				f.setBoolean(te,(Boolean) value);
				break;
			case 1:
				f.setInt(te,(Integer) value);
				break;		
			case 2:
				f.set(te, Util.byte2Direction((Byte) value));
				world.markBlockForUpdate(x, y, z);
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
