package simElectricity.API.Events;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.tileentity.TileEntity;

/**
 * Should be posted when a tileEntity is leaving from the energy net,
 * use {@link simElectricity.API.Energy#postTileDetachEvent(net.minecraft.tileentity.TileEntity) Energy.postTileDetachEvent()}
 */
public class TileDetachEvent extends Event {
    public TileEntity energyTile;

    public TileDetachEvent(TileEntity energyTile) {
        this.energyTile = energyTile;
    }
}
