package simelectricity.essential.client.grid;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISEPowerPole {
	@SideOnly(Side.CLIENT)
	void updateRenderInfo();
	@SideOnly(Side.CLIENT)
	PowerPoleRenderHelper getRenderHelper();
}
