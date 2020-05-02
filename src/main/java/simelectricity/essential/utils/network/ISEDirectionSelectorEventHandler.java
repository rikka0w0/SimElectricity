package simelectricity.essential.utils.network;

import net.minecraft.util.Direction;

public interface ISEDirectionSelectorEventHandler {
    void onDirectionSelected(Direction direction, int mouseButton);
}
