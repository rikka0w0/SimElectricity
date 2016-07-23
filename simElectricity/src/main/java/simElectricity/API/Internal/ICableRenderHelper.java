package simElectricity.API.Internal;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public interface ICableRenderHelper {
    /**
     * Use by cables to determine whether it has been connected to the tileEntity of a specified direction or not
     */
	boolean canConnect(TileEntity tileEntity, ForgeDirection direction);
}
