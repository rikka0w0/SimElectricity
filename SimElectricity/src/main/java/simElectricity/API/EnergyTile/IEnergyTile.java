package simElectricity.API.EnergyTile;

import net.minecraftforge.common.util.ForgeDirection;

public interface IEnergyTile extends ICircuitComponent {
	/**Return a side that is designed to accept power input or output*/
	ForgeDirection getFunctionalSide();
	
	/**Called when the functional side is going to be set*/
	void setFunctionalSide(ForgeDirection newFunctionalSide);
	
	/**Usually called by the wrench, to determine set or not*/
	boolean canSetFunctionalSide(ForgeDirection newFunctionalSide);
}