package simelectricity.api.tile;

import net.minecraft.util.EnumFacing;
import simelectricity.api.node.ISESubComponent;

public interface ISEWireTile {
    ISESubComponent getWireOnSide(EnumFacing side);
}
