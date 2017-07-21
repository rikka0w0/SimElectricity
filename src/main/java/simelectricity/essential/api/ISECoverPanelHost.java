package simelectricity.essential.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;

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
	ForgeDirection getSelectedSide(EntityPlayer player, ForgeDirection side);
	
	ISECoverPanel getCoverPanelOnSide(ForgeDirection side);
	
	/**
	 * Handle on SERVER side ONLY!
	 * @param side
	 * @param coverPanel
	 * @return
	 */
	void installCoverPanel(ForgeDirection side, ISECoverPanel coverPanel);
	
	boolean canInstallCoverPanelOnSide(ForgeDirection side, ISECoverPanel coverPanel);
}
