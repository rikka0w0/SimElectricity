package simelectricity.essential.api;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import simelectricity.api.blockentity.ISECableBlockEntity;

public interface ISEGenericCable extends ISECoverPanelHost, ISECableBlockEntity, ISEChunkWatchSensitiveBlockEntity {
	public static ModelProperty<ISEGenericCable> prop = new ModelProperty<>();
	
    /**
     * Called by cable render (may be custom implementation) to
     * determine if the cable block has connection on the given side
     * @param side
     * @return ture if electrically connected
     */
    boolean connectedOnSide(Direction side);
}
