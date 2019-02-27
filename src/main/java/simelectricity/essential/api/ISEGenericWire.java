package simelectricity.essential.api;

import net.minecraft.util.EnumFacing;
import simelectricity.api.tile.ISEWireTile;

public interface ISEGenericWire extends ISEWireTile, ISEChunkWatchSensitiveTile{
    boolean connectedOnSide(EnumFacing side, EnumFacing to);
    float getWireThickness(EnumFacing side);
    void addBranch(EnumFacing side, EnumFacing to);
    void removeBranch(EnumFacing side, EnumFacing to);
}
