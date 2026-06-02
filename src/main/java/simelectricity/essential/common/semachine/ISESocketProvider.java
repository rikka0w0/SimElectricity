package simelectricity.essential.common.semachine;

import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public interface ISESocketProvider {	
	public static ModelProperty<ISESocketProvider> prop = new ModelProperty<>();

    /**
     * @return iconIndex: <0: nothing, 0: LV
     */
    @OnlyIn(Dist.CLIENT)
    int getSocketIconIndex(Direction side);
}
