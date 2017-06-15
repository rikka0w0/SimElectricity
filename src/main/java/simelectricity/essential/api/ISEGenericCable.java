package simelectricity.essential.api;

import net.minecraftforge.common.util.ForgeDirection;

public interface ISEGenericCable {
	void onCableRenderingUpdateRequested();
	boolean connectedOnSide(ForgeDirection side);
	ISECoverPanel getCoverPanelOnSide(ForgeDirection side);
	
	/**
	 * Handle on SERVER side ONLY!
	 * @param side
	 * @param coverPanel
	 * @return
	 */
	void installCoverPanel(ForgeDirection side, ISECoverPanel coverPanel);
}
