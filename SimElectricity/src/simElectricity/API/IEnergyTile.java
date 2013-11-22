package simElectricity.API;

import net.minecraftforge.common.ForgeDirection;

public interface IEnergyTile extends IBaseComponent {
	/**Return the max safety input voltage*/
	float getMaxSafeVoltage();

	/**When the input voltage exceed the safety range, do explosions here*/
	void onOverVoltage();

	/**For generator etc., return true; for machine, return false*/
	boolean canEmitEnergy(ForgeDirection forgeDirection);
	
	/**For generator etc., return false; for machine, return true*/
	boolean canSinkEnergy(ForgeDirection forgeDirection);
	
	/**Return 0 for sink(typically machines), other value for source(e.g generator)*/

	float getOutputVoltage();
	
	float getInternalResistance();
}
