package simElectricity.API.Common.Blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.EnergyTile.IConductor;

import java.util.Arrays;

public class AutoFacing {

    /**
     * @param tileEntity       tileEntity
     * @param defaultDirection default direction
     *
     * @return valid conductor direction. If there is no conductor nearby, return default direction.
     */
    public static ForgeDirection autoConnect(TileEntity tileEntity, ForgeDirection defaultDirection) {
        return autoConnect(tileEntity, defaultDirection, new ForgeDirection[] {});
    }

    /**
     * Exception version of auto-facing
     *
     * @param tileEntity       tileEntity
     * @param defaultDirection default direction
     * @param exception        exception direction
     *
     * @return valid conductor direction. If there is no conductor nearby, return default direction
     *
     * @see simElectricity.Common.Blocks.BlockSwitch
     */
    public static ForgeDirection autoConnect(TileEntity tileEntity, ForgeDirection defaultDirection, ForgeDirection exception) {
        return autoConnect(tileEntity,defaultDirection,new ForgeDirection[] {exception});
    }

    /**
     * Exceptions array version of auto-facing
     *
     * @param tileEntity       tileEntity
     * @param defaultDirection default direction
     * @param exceptions       exception directions array
     *
     * @return valid conductor direction. If there is no conductor nearby, return default direction.
     */
    public static ForgeDirection autoConnect(TileEntity tileEntity, ForgeDirection defaultDirection, ForgeDirection[] exceptions) {
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if (tileEntity.getWorldObj().getTileEntity(tileEntity.xCoord + direction.offsetX,
                    tileEntity.yCoord + direction.offsetY,
                    tileEntity.zCoord + direction.offsetZ) instanceof IConductor
                    && !Arrays.asList(exceptions).contains(direction))
                return direction;
        }
        return defaultDirection;
    }
}
