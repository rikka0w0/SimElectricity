package SimElectricity.API;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.Event;

public class TileAttachEvent extends Event{
	public TileEntity energyTile;
	public TileAttachEvent(TileEntity energyTile){
		this.energyTile=energyTile;
	}
}
