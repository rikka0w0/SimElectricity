package simelectricity.essential.grid;


import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.client.grid.TransmissionTowerRenderHelper;

public interface ISETransmissionTower {
	@SideOnly(Side.CLIENT)
	void updateRenderInfo();
	@SideOnly(Side.CLIENT)
	TransmissionTowerRenderHelper getRenderHelper();
	@SideOnly(Side.CLIENT)
	int getRotation();
}
