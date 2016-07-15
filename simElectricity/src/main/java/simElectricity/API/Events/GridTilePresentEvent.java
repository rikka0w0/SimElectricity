package simElectricity.API.Events;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.eventhandler.Event;

public class GridTilePresentEvent extends Event {
	public TileEntity te;
	
	
	public GridTilePresentEvent(TileEntity te){
		this.te = te;
	}
}
