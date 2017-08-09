package simelectricity.api;

import net.minecraft.util.EnumFacing;

public interface ISECrowbarTarget {
	boolean canCrowbarBeUsed(EnumFacing side);
	//Server only
	void onCrowbarAction(EnumFacing side, boolean isCreativePlayer);
}
