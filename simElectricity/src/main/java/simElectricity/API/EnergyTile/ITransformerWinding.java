package simElectricity.API.EnergyTile;

import net.minecraftforge.common.util.ForgeDirection;

/** This class represents either primary or secondary of a transformer */
public interface ITransformerWinding extends IBaseComponent{
	float getRatio();
	
	boolean isPrimary();
	
	ITransformer getCore();
}