package simelectricity.essential.api;

import net.minecraft.core.Direction;
import net.minecraftforge.client.model.data.ModelProperty;
import simelectricity.api.tile.ISECableTile;

public interface ISEGenericCable extends ISECoverPanelHost, ISECableTile, ISEChunkWatchSensitiveTile {
	public static ModelProperty<ISEGenericCable> prop = new ModelProperty<>();
	
    /**
     * Called by cable render (may be custom implementation) to
     * determine if the cable block has connection on the given side
     * @param side
     * @return ture if electrically connected
     */
    boolean connectedOnSide(Direction side);
}
