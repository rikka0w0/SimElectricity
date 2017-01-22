package simElectricity.Common;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import simElectricity.API.ISEConnectable;
import simElectricity.API.SEAPI;
import simElectricity.API.Tile.ISECableTile;
import simElectricity.API.ISEWrenchable;
import simElectricity.API.Internal.ICableRenderHelper;
import simElectricity.API.Tile.ISETile;

public class CableRenderHelper implements ICableRenderHelper {
	@Override
	public boolean canConnect(TileEntity tileEntity, ForgeDirection direction) {
        TileEntity ent = SEAPI.utils.getTileEntityonDirection(tileEntity, direction);

        if (ent instanceof ISECableTile) {
            if (tileEntity instanceof ISECableTile) {
                if (((ISECableTile) ent).getColor() == 0 ||
                        ((ISECableTile) tileEntity).getColor() == 0 ||
                        ((ISECableTile) ent).getColor() == ((ISECableTile) tileEntity).getColor()) {
                    return true;
                }
            } else {
                return true;
            }

        } else if (ent instanceof ISEWrenchable) {
            ForgeDirection functionalSide = ((ISEWrenchable) ent).getFunctionalSide();

            if (direction == functionalSide.getOpposite())
                return true;

        } else if (ent instanceof ISEConnectable) {
            if (((ISEConnectable) ent).canConnectOnSide(direction.getOpposite()))
                return true;
        } else if (ent instanceof ISETile){
            if (((ISETile) ent).getComponent(direction.getOpposite()) != null)
                return true;	
        }

        return false;
    }
}
