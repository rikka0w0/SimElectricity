package simElectricity.API.EnergyTile;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * This interface allows wires to connect to this block (Just VISUALLY connected, do NOT impact the energyNet simulation!)
 */
public interface IConnectable {
    boolean canConnectOnSide(ForgeDirection side);
}
