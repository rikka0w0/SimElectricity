package simElectricity.API.Events;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.tileentity.TileEntity;

/**
 * Should be posted when a tileEntity has something changed, ie. output voltage, resistance..., use Util.postTileChangeEvent()
 */
public class TileChangeEvent extends Event {
    public TileEntity energyTile;

    public TileChangeEvent(TileEntity energyTile) {
        this.energyTile = energyTile;
    }
}
