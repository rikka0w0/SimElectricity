package simelectricity.essential.client.grid;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISEPowerPole {
    /**
     * Called by network methods to update appearance, if PowerPoleRenderHelper is null, then attempt to create it in this method
     */
    @SideOnly(Side.CLIENT)
    void updateRenderInfo();

    /**
     * @return an immutable instance of PowerPoleRenderHelper, created in updateRenderInfo()
     */
    @SideOnly(Side.CLIENT)
    PowerPoleRenderHelper getRenderHelper();
}
