package simElectricity.Blocks;

import simElectricity.API.Energy;
import simElectricity.API.ISyncPacketHandler;
import simElectricity.API.Common.TileStandardSEMachine;

public class TileAdjustableResistor extends TileStandardSEMachine implements ISyncPacketHandler{
	public float resistance=1000;
	public float powerConsumed=0;
	public float power=0;
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if(worldObj.isRemote)
			return;
		
		power=Energy.getPower(this);
		powerConsumed+=power/20F;
	}
	
	@Override
	public void onClient2ServerUpdate(String field, Object value, short type) {
		if(field.contains("resistance"))	
			Energy.postTileChangeEvent(this);
	}

	@Override
	public void onServer2ClientUpdate(String field, Object value, short type) {}
	
	@Override
	public float getMaxSafeVoltage() {return 0;}

	@Override
	public void onOverVoltage() {}

	@Override
	public float getOutputVoltage() {return 0;}

	@Override
	public float getResistance() {return resistance;}

	@Override
	public int getMaxPowerDissipation() {return 0;}

	@Override
	public void onOverloaded() {}

	@Override
	public int getInventorySize() {return 0;}

}
