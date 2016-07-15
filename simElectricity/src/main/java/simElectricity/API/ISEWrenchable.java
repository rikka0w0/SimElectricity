package simElectricity.API;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Implement this interface in a tileEntity to allow the wrench to change its functional side
 */
public interface ISEWrenchable {
    /**
     * Return a side that is designed to interact with the energyNet
     */
    ForgeDirection getFunctionalSide();

    /**
     * Called when the functional side is going to be set
     */
    void setFunctionalSide(ForgeDirection newFunctionalSide);

    boolean canSetFunctionalSide(ForgeDirection newFunctionalSide);
}
