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
}
