package simelectricity.api;

import net.minecraftforge.common.util.ForgeDirection;

public interface ISECrowbarTarget {
	boolean canCrowbarBeUsed(ForgeDirection side);
	//Server only
	void onCrowbarAction(ForgeDirection side, boolean isCreativePlayer);
}
