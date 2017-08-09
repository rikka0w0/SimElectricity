package simelectricity.essential.api;

import simelectricity.essential.api.coverpanel.ISECoverPanel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

public interface ISECoverPanelHost {
	/**
	 * Use custom raytrace to determine which cover panel is actually selected by the player
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param player
	 * @param side the side which is closest to the player
	 * @return the side which the player is actually looking at, UNKNOWN - center
	 */
	EnumFacing getSelectedSide(EntityPlayer player, EnumFacing side);
	
	ISECoverPanel getCoverPanelOnSide(EnumFacing side);
	
	/**
	 * Handle on SERVER side ONLY!
	 * @param side
	 * @param coverPanel
	 * @return
	 */
	void installCoverPanel(EnumFacing side, ISECoverPanel coverPanel);
	
	boolean canInstallCoverPanelOnSide(EnumFacing side, ISECoverPanel coverPanel);
}
