package SimElectricity.API;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.Event;

public class TileChangeEvent extends Event{
	public TileEntity energyTile;
	public TileChangeEvent(TileEntity energyTile){
		this.energyTile=energyTile;
	}
}
