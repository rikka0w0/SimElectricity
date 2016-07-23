package simElectricity.Common;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import simElectricity.API.Util;
import simElectricity.API.EnergyTile.ISEConductor;
import simElectricity.API.EnergyTile.ISEConnectable;
import simElectricity.API.EnergyTile.ISESimpleTile;
import simElectricity.API.EnergyTile.ISETile;
import simElectricity.API.Internal.ICableRenderHelper;

public class CableRenderHelper implements ICableRenderHelper {
	@Override
	public boolean canConnect(TileEntity tileEntity, ForgeDirection direction) {
        TileEntity ent = Util.getTileEntityonDirection(tileEntity, direction);

        if (ent instanceof ISEConductor) {
            if (tileEntity instanceof ISEConductor) {
                if (((ISEConductor) ent).getColor() == 0 ||
                        ((ISEConductor) tileEntity).getColor() == 0 ||
                        ((ISEConductor) ent).getColor() == ((ISEConductor) tileEntity).getColor()) {
                    return true;
                }
            } else {
                return true;
            }

        } else if (ent instanceof ISESimpleTile) {
            ForgeDirection functionalSide = ((ISESimpleTile) ent).getFunctionalSide();

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
