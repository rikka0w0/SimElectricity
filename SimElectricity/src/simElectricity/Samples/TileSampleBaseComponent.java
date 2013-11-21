package simElectricity.Samples;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import simElectricity.API.IBaseComponent;
import simElectricity.API.TileAttachEvent;
import simElectricity.API.TileDetachEvent;

public abstract class TileSampleBaseComponent extends TileEntity implements	IBaseComponent {
	protected boolean isAddedToEnergyNet = false;
	
	@Override
	public void updateEntity() {
		if (!worldObj.isRemote && !isAddedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new TileAttachEvent(this));
			this.isAddedToEnergyNet=true;
		}
	}

	@Override
	public void invalidate() {
		if (!worldObj.isRemote & isAddedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new TileDetachEvent(this));
		}
	}
}
