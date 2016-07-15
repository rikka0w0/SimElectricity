package simElectricity.API.EnergyTile;

import simElectricity.API.ISEWrenchable;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * A simple electrical model for machines or generators
 * <p/>
 * This interface should be implemented by a tileEntity
 * <p/>
 * The tileEntity with this interface can only have 1 node in the simulation
 */
public interface ISESimpleTile extends ISEPlaceable, ISEVoltageSource, ISEWrenchable{

}
