package simelectricity.essential.grid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simelectricity.essential.grid.render.TransmissionTowerRenderHelper;

public interface ISETransmissionTower {
	@SideOnly(Side.CLIENT)
	void updateRenderInfo();
	@SideOnly(Side.CLIENT)
	TransmissionTowerRenderHelper getRenderHelper();
	@SideOnly(Side.CLIENT)
	int getRotation();
}
