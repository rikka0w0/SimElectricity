package simElectricity.API.Events;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.eventhandler.Event;

/** Should be posted when a tileEntity has something changed, ie. output voltage, resistance..., use Util.postTileChangeEvent()*/
public class TileChangeEvent extends Event {
	public TileEntity energyTile;

	public TileChangeEvent(TileEntity energyTile) {
		this.energyTile = energyTile;
	}
}
