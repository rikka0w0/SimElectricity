package simElectricity.API.Events;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.eventhandler.Event;

public class GridTileInvalidateEvent extends Event {
	public TileEntity te;
	
	public GridTileInvalidateEvent(TileEntity te){
		this.te = te;
	}
}
