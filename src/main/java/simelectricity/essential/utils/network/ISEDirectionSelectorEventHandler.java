package simelectricity.essential.utils.network;

import net.minecraft.util.EnumFacing;

public interface ISEDirectionSelectorEventHandler {
	public void onDirectionSelected(EnumFacing direction, int mouseButton);
}
