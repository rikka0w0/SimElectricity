package simElectricity.API;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.eventhandler.Event;

public class TileChangeEvent extends Event {
	public TileEntity energyTile;

	public TileChangeEvent(TileEntity energyTile) {
		this.energyTile = energyTile;
	}
}
