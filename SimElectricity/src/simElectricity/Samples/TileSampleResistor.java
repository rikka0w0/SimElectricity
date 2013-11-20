package simElectricity.Samples;

import simElectricity.API.IEnergyTile;

public class TileSampleResistor extends TileSampleBaseComponent implements
		IEnergyTile {

	@Override
	public int getResistance() {
		// TODO Auto-generated method stub
		return 500;
	}

	@Override
	public void onOverloaded() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getMaxPowerDissipation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getMaxSafeVoltage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onOverVoltage() {
		// TODO Auto-generated method stub

	}

	@Override
	public float getOutputVoltage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getInternalResistance() {
		// TODO Auto-generated method stub
		return 10000;
	}

}
