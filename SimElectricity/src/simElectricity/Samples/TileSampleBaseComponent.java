package simElectricity.Samples;

import simElectricity.API.IBaseComponent;
import simElectricity.API.TileAttachEvent;
import simElectricity.API.TileDetachEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

public abstract class TileSampleBaseComponent extends TileEntity implements
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
}
