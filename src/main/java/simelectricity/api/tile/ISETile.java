package simelectricity.api.tile;

import net.minecraft.util.EnumFacing;
import simelectricity.api.node.ISESubComponent;

/**
 * ISETile is a container, it is able to support up to 6 ISESubComponent. TileEntities implement this interface
 * <p/>
 * Can be used to make transformers, regulators, diodes, switches, generators and loads
 */
public interface ISETile {
    /**
     * @return An array of directions that can be used to connect to {link}ISESubComponent
     */
    //public EnumFacing[] getValidDirections();
    ISESubComponent getComponent(EnumFacing side);
}
