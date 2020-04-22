package simelectricity.api.tile;

import net.minecraft.util.Direction;
import simelectricity.api.components.ISEWire;

public interface ISEWireTile extends ISETile{
    ISEWire getWireParam(Direction side);
}
