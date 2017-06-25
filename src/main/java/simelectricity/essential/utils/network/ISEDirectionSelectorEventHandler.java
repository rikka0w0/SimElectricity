package simelectricity.essential.utils.network;

import net.minecraftforge.common.util.ForgeDirection;

public interface ISEDirectionSelectorEventHandler {
	public void onDirectionSelected(ForgeDirection direction, int mouseButton);
}
