package simelectricity.essential.api;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.ModelProperty;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

public interface ISECoverPanelHost {
	public static ModelProperty<ISECoverPanelHost> prop = new ModelProperty();
	
    /**
     * Use custom raytrace to determine which cover panel is actually selected by the player
     *
     * @param player
     * @return the side which the player is actually looking at, null - the cable
     */
	@Nullable
    Direction getSelectedCoverPanel(PlayerEntity player);

    ISECoverPanel getCoverPanelOnSide(Direction side);

    /**
     * Actual installation action should be handled on SERVER side ONLY!
     *
     * @param side
     * @param coverPanel
     * @param simulated the cover panel will only be installed if simulated is false
     * @return true if success or can be installed. 
     * Implementers should return false if a cover panel is already installed on the given side.
     */
    boolean installCoverPanel(Direction side, ISECoverPanel coverPanel, boolean simulated);

    /**
     * Actual removal action should be handled on SERVER side ONLY!
     *
     * @param coverPanel
     * @param simulated the cover panel will only be removed if simulated is false
     * @return true if success or can be removed, if coverpanel does not exist on that side, return false.
     */
    boolean removeCoverPanel(Direction side, boolean simulated);
}
