package simElectricity.Blocks;

import simElectricity.API.ISyncPacketHandler;
import simElectricity.API.TileStandardGenerator;
import simElectricity.API.Util;

public class TileQuantumGenerator extends TileStandardGenerator implements ISyncPacketHandler{
	@Override
	public void onClient2ServerUpdate(String field, Object value, short type) {
		if(field.contains("outputVoltage")|field.contains("outputResistance"))	
			Util.postTileChangeEvent(this);
	}

	@Override
	public void onServer2ClientUpdate(String field, Object value, short type) {}
}
