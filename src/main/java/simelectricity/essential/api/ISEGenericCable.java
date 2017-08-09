package simelectricity.essential.api;

import net.minecraft.util.EnumFacing;

public interface ISEGenericCable extends ISECoverPanelHost{
	void onCableRenderingUpdateRequested();
	boolean connectedOnSide(EnumFacing side);
}
