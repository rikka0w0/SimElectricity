package simElectricity.API;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

public abstract class BaseComponent extends TileEntity implements
		IBaseComponent {
	public boolean isAddedToEnergyNet = false;

	@Override
	public void validate() {
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

	@Override
	public BaseComponent[] getNeighboringNodes(int x, int y, int z) {
		BaseComponent[] result = new BaseComponent[7];
		int i = 0;
		TileEntity tmp;

		tmp = worldObj.getBlockTileEntity(x + 1, y, z);
		if (tmp instanceof IBaseComponent) {
			result[i++] = (BaseComponent) tmp;
		}
		tmp = worldObj.getBlockTileEntity(x - 1, y, z);
		if (tmp instanceof IBaseComponent) {
			result[i++] = (BaseComponent) tmp;
		}
		tmp = worldObj.getBlockTileEntity(x, y + 1, z);
		if (tmp instanceof IBaseComponent) {
			result[i++] = (BaseComponent) tmp;
		}
		tmp = worldObj.getBlockTileEntity(x, y - 1, z);
		if (tmp instanceof IBaseComponent) {
			result[i++] = (BaseComponent) tmp;
		}
		tmp = worldObj.getBlockTileEntity(x, y, z + 1);
		if (tmp instanceof IBaseComponent) {
			result[i++] = (BaseComponent) tmp;
		}
		tmp = worldObj.getBlockTileEntity(x, y, z - 1);
		if (tmp instanceof IBaseComponent) {
			result[i++] = (BaseComponent) tmp;
		}

		return result;
	}

	@Override
	public int getNodeType() {
		if (!(this instanceof IPowerSource)) {
			return powerSource;
		} else if (!(this instanceof IPowerSink)) {
			return powerSink;
		} else if (!(this instanceof IConductor)) {
			return conductor;
		}
		return 0;
	}
}
