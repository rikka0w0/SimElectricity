package simElectricity.API.EnergyTile;

import net.minecraftforge.common.util.ForgeDirection;

public interface ITranformer {
	ForgeDirection getInputSide();
	
	ForgeDirection getOutputSide();
	
	ITransformerPrimary getPrimary();
	
	ITransformerSecondary getSecondary();
}
