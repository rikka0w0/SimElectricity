package simElectricity.API;

import net.minecraftforge.common.util.*;

public interface IEnergyTile extends IBaseComponent {
	/**Return the max safety input voltage*/
	float getMaxSafeVoltage();

	/**When the input voltage exceed the safety range, do explosions here*/
	void onOverVoltage();

	/**Return a side that is designed to accept power input or output*/
	ForgeDirection getFunctionalSide();
	
	/**Called when the functional side is going to be set*/
	void setFunctionalSide(ForgeDirection newFunctionalSide);
	
	/**Usually called by the wrench, to determine set or not*/
	boolean canSetFunctionalSide(ForgeDirection newFunctionalSide);
	
	/**Return 0 for sink(typically machines), other value for source(e.g generator)*/
	float getOutputVoltage();
}