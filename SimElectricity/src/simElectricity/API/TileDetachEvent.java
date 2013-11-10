package simElectricity.API;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.Event;

public class TileDetachEvent extends Event {
	public TileEntity energyTile;

	public TileDetachEvent(TileEntity energyTile) {
		this.energyTile = energyTile;
	}
}
