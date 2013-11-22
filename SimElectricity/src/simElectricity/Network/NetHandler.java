package simElectricity.Network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class NetHandler implements IPacketHandler{
	public static final String NET_CHANNEL_CLIENT = "seclient",
			NET_CHANNEL_SERVER = "seserver";
	public static final byte Net_ID_TileEntitySync=0;
	private static HashMap<Byte, IChannelProcess> channels = new HashMap();
	
	
	@Override
	public void onPacketData(INetworkManager manager,Packet250CustomPayload packet, Player player) {

		DataInputStream inputStream = new DataInputStream(
				new ByteArrayInputStream(packet.data));

		byte i = -1;
		try {
			i = inputStream.readByte();
		} catch (IOException e) {
			e.printStackTrace();
		}
		IChannelProcess p = channels.get(i);
		if (packet.channel.equals(NET_CHANNEL_CLIENT)|| packet.channel.equals(NET_CHANNEL_SERVER)) {
			if (p != null)
				p.onPacketData(inputStream, player);
		}
	}

	public static boolean addChannel(byte channel, IChannelProcess process) {
		if (channels.containsKey(channel)) {
			return false;
		}
		channels.put(channel, process);
		return true;
	}

	public static byte getUniqueChannelID() {
		for (byte i = 0; i < Byte.MAX_VALUE; i++) {
			if (!channels.containsKey(i))
				return i;
		}
		return -1;
	}

	public static ByteArrayOutputStream getStream(short id, int size) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(size + 1);
		DataOutputStream stream = new DataOutputStream(bos);
		try {
			stream.writeByte(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bos;
	}
}
