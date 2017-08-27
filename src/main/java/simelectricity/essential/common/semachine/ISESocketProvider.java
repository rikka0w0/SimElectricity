package simelectricity.essential.common.semachine;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISESocketProvider {
    int numOfSockets = 5;

    /**
     * @return iconIndex: <0: nothing, 0: LV
     */
    @SideOnly(Side.CLIENT)
    int getSocketIconIndex(EnumFacing side);
}
