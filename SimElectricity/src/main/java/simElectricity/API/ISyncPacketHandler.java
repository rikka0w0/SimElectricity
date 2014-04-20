package simElectricity.API;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface ISyncPacketHandler {
	/** Called when a updating packet is sending from a client to the server, handled by the server*/
	public void onClient2ServerUpdate(String field, Object value, short type);
	
	/** Called when a updating packet is sending from the server to a client, handled by a client */
	public void onServer2ClientUpdate(String field, Object value, short type);
}
