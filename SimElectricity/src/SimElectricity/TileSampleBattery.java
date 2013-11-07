package SimElectricity;

import SimElectricity.API.IPowerSource;
import net.minecraft.tileentity.TileEntity;

public class TileSampleBattery extends TileEnergyBase implements IPowerSource {

	
	
	@Override
	public int getResistance() {
		// TODO Auto-generated method stub
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
	public int getOutputVoltage() {
		// TODO Auto-generated method stub
		return 12;
	}

}
