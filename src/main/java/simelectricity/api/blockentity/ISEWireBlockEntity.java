package simelectricity.api.blockentity;

import net.minecraft.core.Direction;
import simelectricity.api.components.ISEWire;

public interface ISEWireBlockEntity extends ISEBlockEntity{
    ISEWire getWireParam(Direction side);
}
