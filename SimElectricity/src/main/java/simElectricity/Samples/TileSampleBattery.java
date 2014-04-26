package simElectricity.Samples;

import net.minecraftforge.common.util.ForgeDirection;

public class TileSampleBattery extends TileSampleEnergyTile{
	
	@Override
	public float getResistance() {
		return 1;
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
	public ForgeDirection getFunctionalSide() {
		return functionalSide;
	}

	@Override
	public void setFunctionalSide(ForgeDirection newFunctionalSide) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canSetFunctionalSide(ForgeDirection newFunctionalSide) {
		// TODO Auto-generated method stub
		return false;
	}
}
