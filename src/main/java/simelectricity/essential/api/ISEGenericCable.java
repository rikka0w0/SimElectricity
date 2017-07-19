package simelectricity.essential.api;

import net.minecraftforge.common.util.ForgeDirection;

public interface ISEGenericCable extends ISECoverPanelHost{
	void onCableRenderingUpdateRequested();
	boolean connectedOnSide(ForgeDirection side);
}
