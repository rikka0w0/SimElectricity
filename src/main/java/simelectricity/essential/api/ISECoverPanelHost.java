package simelectricity.essential.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

public interface ISECoverPanelHost {
    /**
     * Use custom raytrace to determine which cover panel is actually selected by the player
     *
     * @param player
     * @return the side which the player is actually looking at, null - the cable
     */
    ISECoverPanel getSelectedCoverPanel(PlayerEntity player);

    ISECoverPanel getCoverPanelOnSide(Direction side);

    /**
     * Handle on SERVER side ONLY!
     *
     * @param side
     * @param coverPanel
     * @return
     */
    void installCoverPanel(Direction side, ISECoverPanel coverPanel);

    boolean canInstallCoverPanelOnSide(Direction side, ISECoverPanel coverPanel);

    /**
     * Handle on SERVER side ONLY!
     *
     * @param coverPanel
     * @param dropItem
     * @return true if success
     */
    boolean removeCoverPanel(ISECoverPanel coverPanel, boolean dropItem);
}
