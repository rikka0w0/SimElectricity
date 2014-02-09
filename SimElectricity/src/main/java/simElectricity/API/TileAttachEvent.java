package simElectricity.API;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.eventhandler.Event;

public class TileAttachEvent extends Event {
	public TileEntity energyTile;

	public TileAttachEvent(TileEntity energyTile) {
		this.energyTile = energyTile;
	}
}
