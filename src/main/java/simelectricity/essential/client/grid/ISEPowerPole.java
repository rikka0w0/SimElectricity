package simelectricity.essential.client.grid;

import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ISEPowerPole {
	@OnlyIn(Dist.CLIENT)
    BlockPos[] getNeighborPosArray();
    
	@OnlyIn(Dist.CLIENT)
    BlockPos getAccessoryPos();

    /**
     * @return an immutable instance of PowerPoleRenderHelper, created before updateRenderInfo()
     */
	@OnlyIn(Dist.CLIENT)
    PowerPoleRenderHelper getRenderHelper();
}
