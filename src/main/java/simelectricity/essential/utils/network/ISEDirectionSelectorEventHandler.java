package simelectricity.essential.utils.network;

import net.minecraft.util.EnumFacing;

public interface ISEDirectionSelectorEventHandler {
    void onDirectionSelected(EnumFacing direction, int mouseButton);
}
