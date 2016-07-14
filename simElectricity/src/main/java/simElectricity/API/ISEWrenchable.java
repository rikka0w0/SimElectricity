package simElectricity.API;

import net.minecraftforge.common.util.ForgeDirection;

public interface ISEWrenchable {
    /**
     * Return a side that is designed to interact with energyNet
     */
    ForgeDirection getFunctionalSide();

    /**
     * Called when the functional side is going to be set
     */
    void setFunctionalSide(ForgeDirection newFunctionalSide);

    /**
     * Usually called by the wrench, to determine set or not
     */
    boolean canSetFunctionalSide(ForgeDirection newFunctionalSide);
}
