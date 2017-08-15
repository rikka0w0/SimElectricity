package simelectricity.essential.client.grid;


import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISETransmissionTower {
	@SideOnly(Side.CLIENT)
	void updateRenderInfo();
	@SideOnly(Side.CLIENT)
	TransmissionTowerRenderHelper getRenderHelper();
	@SideOnly(Side.CLIENT)
	int getRotation();
}
