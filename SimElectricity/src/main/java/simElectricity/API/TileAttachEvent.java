package simElectricity.API;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.eventhandler.Event;

/** Should be posted when a tileEntity is joining the energy net ,use Util.postTileAttachEvent()*/
public class TileAttachEvent extends Event {
	public TileEntity energyTile;

	public TileAttachEvent(TileEntity energyTile) {
		this.energyTile = energyTile;
	}
}
