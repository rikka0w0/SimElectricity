package simElectricity.Network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Field;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TileEntityFieldUpdatePacket implements IChannelProcess{
	public static void sendSyncPacket(TileEntity te,String field) {
		if (te.worldObj==null)
			return;
	
		int dataSize = 32;
		ByteArrayOutputStream bos = NetHandler.getStream(
				NetHandler.Net_ID_TileEntitySync, dataSize);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(te.xCoord);
			outputStream.writeShort(te.yCoord);
			outputStream.writeInt(te.zCoord);
			
			outputStream.writeInt(te.getClass().hashCode());
			outputStream.writeShort(field.length());
			outputStream.writeChars(field);
			
			Field f=te.getClass().getField(field);
			if(f.getType()==boolean.class){
				outputStream.writeByte(0);
				outputStream.writeBoolean(f.getBoolean(te));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = NetHandler.NET_CHANNEL_CLIENT;
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		int dimension = te.worldObj.provider.dimensionId;
		PacketDispatcher.sendPacketToAllInDimension(packet, dimension);
	}

	@Override
	public void onPacketData(DataInputStream stream, Player player) {
		try {
			int x = stream.readInt(), 
				y = stream.readShort(),
				z = stream.readInt();
			
			World world = ((EntityPlayer)player).worldObj;
			TileEntity te = world.getBlockTileEntity(x, y, z);
			if (te == null) 
				return;
			
			int hash=stream.readInt();
			if (te.getClass().hashCode()!=hash)
				return;
			
			String field="";
			short length=stream.readShort();
			for (int i=0;i<length;i++)
				field+=stream.readChar();
			
			switch (stream.readByte()){
				case 0:
					Field f=te.getClass().getField(field);
					Boolean b=stream.readBoolean();
					f.setBoolean(te, b);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
