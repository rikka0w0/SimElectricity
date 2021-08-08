package simelectricity.essential.utils.network;

import net.minecraft.core.Direction;

public interface ISEDirectionSelectorEventHandler {
    void onDirectionSelected(Direction direction, int mouseButton);
}
