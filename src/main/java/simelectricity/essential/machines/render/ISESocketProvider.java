package simelectricity.essential.machines.render;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.EnumFacing;

public interface ISESocketProvider {
	/**
	 * @return iconIndex: <0: nothing, 0: LV
	 */
	@SideOnly(Side.CLIENT)
	public int getSocketIconIndex(EnumFacing side);
}
