package simelectricity.essential.common.semachine;

import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelProperty;

public interface ISESocketProvider {
	@Deprecated
    int numOfSockets = 5;
	
	public static ModelProperty<ISESocketProvider> prop = new ModelProperty<>();

    /**
     * @return iconIndex: <0: nothing, 0: LV
     */
    @OnlyIn(Dist.CLIENT)
    int getSocketIconIndex(Direction side);
}
