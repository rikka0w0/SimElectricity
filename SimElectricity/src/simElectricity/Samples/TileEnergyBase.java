package simElectricity.Samples;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import simElectricity.API.TileAttachEvent;
import simElectricity.API.TileDetachEvent;

public abstract class TileEnergyBase extends TileEntity {
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
