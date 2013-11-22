package simElectricity.Samples;

import net.minecraftforge.common.ForgeDirection;
import simElectricity.API.IEnergyTile;

public class TileSampleBattery extends TileSampleBaseComponent implements
		IEnergyTile {

	@Override
	public int getResistance() {
		return 0;
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
	public float getOutputVoltage() {
		// TODO Auto-generated method stub
		return 12;
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
	public boolean canEmitEnergy(ForgeDirection forgeDirection) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canSinkEnergy(ForgeDirection forgeDirection) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float getInternalResistance() {
		return 1;
	}

}
