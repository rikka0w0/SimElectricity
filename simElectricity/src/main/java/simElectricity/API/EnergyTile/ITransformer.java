package simElectricity.API.EnergyTile;

import net.minecraftforge.common.util.ForgeDirection;

public interface ITransformer {
	ForgeDirection getInputSide();
	
	ForgeDirection getOutputSide();
	
	ITransformerWinding getPrimary();
	
	ITransformerWinding getSecondary();
}
