package simelectricity.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;

public interface ISECrowbarTarget {
	ForgeDirection getSelectedSide(EntityPlayer player, ForgeDirection side);
	boolean canCrowbarBeUsed(ForgeDirection side);
	//Server only
	void onCrowbarAction(ForgeDirection side, boolean isCreativePlayer);
}
