package simelectricity.essential.client.grid;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISEPowerPole {
    /**
     * Call getRenderHelper().updateRenderData(neighbors);
     */
    @SideOnly(Side.CLIENT)
    void updateRenderInfo();

    /**
     * @return an immutable instance of PowerPoleRenderHelper, created before updateRenderInfo()
     */
    @Nonnull
    @SideOnly(Side.CLIENT)
    PowerPoleRenderHelper getRenderHelper();
}
