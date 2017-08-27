package simelectricity.essential.utils.network;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISEContainerUpdate {
    @SideOnly(Side.CLIENT)
    void onDataArrivedFromServer(Object[] data);
}
