package simElectricity.API.Common.Blocks;

import simElectricity.API.EnergyTile.IConductor;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class AutoFacing {
	public static ForgeDirection autoConnect(TileEntity te, ForgeDirection defaultDirection){
		for (ForgeDirection direction: ForgeDirection.VALID_DIRECTIONS){
			if (te.getWorldObj().getTileEntity(te.xCoord + direction.offsetX,
											   te.yCoord + direction.offsetY,
											   te.zCoord + direction.offsetZ) instanceof IConductor)
				return direction;
		}
		
		return defaultDirection;
	}
	
	public static ForgeDirection autoConnect(TileEntity te, ForgeDirection defaultDirection, ForgeDirection exception){
		for (ForgeDirection direction: ForgeDirection.VALID_DIRECTIONS){
			if (te.getWorldObj().getTileEntity(te.xCoord + direction.offsetX,
											   te.yCoord + direction.offsetY,
											   te.zCoord + direction.offsetZ) instanceof IConductor
											   && direction != exception)
				return direction;
		}
		
		return defaultDirection;
	}	
}
