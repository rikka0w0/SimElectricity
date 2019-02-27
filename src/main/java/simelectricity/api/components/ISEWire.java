package simelectricity.api.components;

import net.minecraft.util.EnumFacing;

public interface ISEWire extends ISECableBase{
    /**
     * @return false to block any connection from the given side
     */
    boolean hasBranchOnSide(EnumFacing side);
}
