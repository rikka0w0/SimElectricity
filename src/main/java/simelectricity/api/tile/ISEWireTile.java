package simelectricity.api.tile;

import net.minecraft.util.EnumFacing;
import simelectricity.api.components.ISEWire;

public interface ISEWireTile {
    ISEWire getWireOnSide(EnumFacing side);
}
