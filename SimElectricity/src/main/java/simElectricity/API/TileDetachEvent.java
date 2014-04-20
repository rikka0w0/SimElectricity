package simElectricity.API;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.tileentity.TileEntity;

/** Should be posted when a tileEntity is leaving from the energy net, use Util.postTileDetachEvent()*/
public class TileDetachEvent extends Event {
	public TileEntity energyTile;

	public TileDetachEvent(TileEntity energyTile) {
		this.energyTile = energyTile;
	}
}
