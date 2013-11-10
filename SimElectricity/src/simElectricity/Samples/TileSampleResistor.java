package simElectricity.Samples;

import simElectricity.API.BaseComponent;
import simElectricity.API.IPowerSink;

public class TileSampleResistor extends BaseComponent implements IPowerSink {

	@Override
	public int getResistance() {
		// TODO Auto-generated method stub
		return 10000;
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
	public int getMaxSafeVoltage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onOverVoltage() {
		// TODO Auto-generated method stub

	}

}
