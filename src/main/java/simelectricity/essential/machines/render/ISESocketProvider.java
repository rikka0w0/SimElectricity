package simelectricity.essential.machines.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.util.ForgeDirection;

public interface ISESocketProvider {
	/**
	 * @return iconIndex: <0: nothing, 0: LV
	 */
	@SideOnly(Side.CLIENT)
	public int getSocketIconIndex(ForgeDirection side);
}
