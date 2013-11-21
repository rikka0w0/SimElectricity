package simElectricity.Samples;

import simElectricity.API.IConductor;

public class TileSampleConductor extends TileSampleBaseComponent implements
		IConductor {

	@Override
	public int getResistance() {
		return 500;
	}

	@Override
	public void onOverloaded() {
		worldObj.createExplosion(null, xCoord, yCoord, zCoord, 0, true);
	}

	@Override
	public int getMaxPowerDissipation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInsulationBreakdownVoltage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onInsulationBreakdown() {
		// TODO Auto-generated method stub

	}

}
