package simElectricity.API;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * A tile entity should implement this, if its facing want to be changed by the glove item, all machines are recommended to implement this!
 */
public interface ISidedFacing {
    /**
     * Return the facing(Should have a line of "private ForgeDirection facing = ForgeDirection.NORTH;" in the tile entity class)
     */
    ForgeDirection getFacing();

    /**
     * Usually contains a line of "facing = newFacing;"
     */
    void setFacing(ForgeDirection newFacing);

    /**
     * Tell the glove, which facing is allowed or not allowed, directly return false means glove can do nothing!
     */
    boolean canSetFacing(ForgeDirection newFacing);
}
