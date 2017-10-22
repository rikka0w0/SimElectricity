package simelectricity.essential.client.grid;

import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISEPowerPole {
    @SideOnly(Side.CLIENT)
    BlockPos[] getNeighborPosArray();
    
    @SideOnly(Side.CLIENT)
    BlockPos getAccessoryPos();

    /**
     * @return an immutable instance of PowerPoleRenderHelper, created before updateRenderInfo()
     */
    @Nonnull
    @SideOnly(Side.CLIENT)
    PowerPoleRenderHelper getRenderHelper();
}
