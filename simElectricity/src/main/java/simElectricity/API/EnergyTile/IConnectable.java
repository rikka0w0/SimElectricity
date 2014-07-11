package simElectricity.API.EnergyTile;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * This interface allows a wire connect to the block
 */
public interface IConnectable {
    boolean canConnectOnSide(ForgeDirection side);
}
