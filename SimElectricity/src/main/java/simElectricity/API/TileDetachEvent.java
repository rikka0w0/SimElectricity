package simElectricity.API;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.tileentity.TileEntity;

public class TileDetachEvent extends Event {
	public TileEntity energyTile;

	public TileDetachEvent(TileEntity energyTile) {
		this.energyTile = energyTile;
	}
}
