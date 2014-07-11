package simElectricity.API.EnergyTile;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * This interface allows a block to have maximum 6 circuit components in the simulation
 */
public interface IComplexTile extends IConnectable {
    /**
     * Returns the corresponding circuit component on certain side
     */
    public ICircuitComponent getCircuitComponent(ForgeDirection side);
}
