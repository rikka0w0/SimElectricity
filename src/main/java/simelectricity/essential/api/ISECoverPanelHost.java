package simelectricity.essential.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

public interface ISECoverPanelHost {
    /**
     * Use custom raytrace to determine which cover panel is actually selected by the player
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param player
     * @param side   the closest side to the player
     * @return the side which the player is actually looking at, null - center
     */
    ISECoverPanel getSelectedCoverPanel(EntityPlayer player);

    ISECoverPanel getCoverPanelOnSide(EnumFacing side);

    /**
     * Handle on SERVER side ONLY!
     *
     * @param side
     * @param coverPanel
     * @return
     */
    void installCoverPanel(EnumFacing side, ISECoverPanel coverPanel);

    boolean canInstallCoverPanelOnSide(EnumFacing side, ISECoverPanel coverPanel);

    /**
     * Handle on SERVER side ONLY!
     *
     * @param coverPanel
     * @param dropItem
     * @return true if success
     */
    boolean removeCoverPanel(ISECoverPanel coverPanel, boolean dropItem);
}
