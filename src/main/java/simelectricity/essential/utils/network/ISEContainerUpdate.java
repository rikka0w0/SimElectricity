package simelectricity.essential.utils.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface ISEContainerUpdate {
	@SideOnly(Side.CLIENT)
	public void onDataArrivedFromServer(Object[] data);
}
