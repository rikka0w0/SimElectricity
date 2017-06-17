package simelectricity.api.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simelectricity.essential.grid.render.TransmissionTowerRenderHelper;

public interface ITransmissionTower {
	@SideOnly(Side.CLIENT)
	void updateRenderInfo();
	@SideOnly(Side.CLIENT)
	TransmissionTowerRenderHelper getRenderHelper();
	double getInsulatorLength();
	double[] getInsulatorPositionArray();
	int getRotation();
}
