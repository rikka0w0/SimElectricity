package simElectricity.API;

import net.minecraftforge.common.ForgeDirection;

public interface IEnergyTile extends IBaseComponent {
	float getMaxSafeVoltage();

	void onOverVoltage();

	float getInternalResistance();

	// 0 is sink, other is source
	float getOutputVoltage();

	boolean canEmitEnergy(ForgeDirection forgeDirection);
	
	boolean canSinkEnergy(ForgeDirection forgeDirection);
}
