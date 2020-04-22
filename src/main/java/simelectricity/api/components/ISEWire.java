package simelectricity.api.components;

import net.minecraft.util.Direction;

public interface ISEWire extends ISECableBase{
    /**
     * @param side if null, the result will be true if the wire has at least one branch
     * @return true if the wire has a branch on the given side
     */
    boolean hasBranchOnSide(Direction side);
}
