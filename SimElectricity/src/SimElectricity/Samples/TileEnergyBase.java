package SimElectricity.Samples;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import SimElectricity.API.TileAttachEvent;
import SimElectricity.API.TileDetachEvent;

public abstract class TileEnergyBase extends TileEntity {
	public boolean isAddedToEnergyNet = false;

	public void updateEntity() {
		if (!worldObj.isRemote & !isAddedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new TileAttachEvent(this));
			isAddedToEnergyNet = true;
		}
	}

	@Override
	public void invalidate() {
		if (!worldObj.isRemote & isAddedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new TileDetachEvent(this));
			isAddedToEnergyNet = false;
		}
	}

	public TileEnergyBase getNeighboringTileEntity(int direction) {
		TileEntity result = null;
		switch (direction) {
		case 0:
			result = worldObj.getBlockTileEntity(xCoord - 1, yCoord, zCoord);
			break;
		case 1:
			result = worldObj.getBlockTileEntity(xCoord + 1, yCoord, zCoord);
			break;
		case 2:
			result = worldObj.getBlockTileEntity(xCoord, yCoord - 1, zCoord);
			break;
		case 3:
			result = worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
			break;
		case 4:
			result = worldObj.getBlockTileEntity(xCoord, yCoord, zCoord - 1);
			break;
		case 5:
			result = worldObj.getBlockTileEntity(xCoord, yCoord, zCoord + 1);
			break;
		default:
			break;
		}
		
		if(result instanceof TileEnergyBase){
			return (TileEnergyBase) result;
		}
		else 
			return null;
	}
}
