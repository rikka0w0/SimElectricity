package simElectricity.EnergyNet;

import simElectricity.API.EnergyTile.IBaseComponent;
import net.minecraftforge.common.util.ForgeDirection;

/** This class represents either primary or secondary of a transformer */
public interface ITransformerComponent extends IBaseComponent{
	float getRatio();
}