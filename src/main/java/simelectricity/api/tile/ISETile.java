package simelectricity.api.tile;

import net.minecraft.util.EnumFacing;
import simelectricity.api.node.ISESubComponent;

/**
 * ISETile is a container which can support up to 6 ISESubComponents. For TileEntities only.
 * <p/>
 * Host container for transformers, diodes, switches, generators and loads
 */
public interface ISETile {
    /**
     * @return the component on the given side, null if not applicable
     */
    ISESubComponent getComponent(EnumFacing side);
}
