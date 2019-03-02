package simelectricity.api.tile;

import net.minecraft.util.EnumFacing;
import simelectricity.api.components.ISEWire;
import simelectricity.api.node.ISESubComponent;

public interface ISEWireTile {
    ISEWire getWireParam(EnumFacing side);

    ISESubComponent getComponent(EnumFacing side);
}
